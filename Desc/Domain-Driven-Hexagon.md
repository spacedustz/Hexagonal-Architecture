## 헥사고날 아키텍처 구현하기

[Hexagonal Architecture Repository](https://github.com/spacedustz/Hexagonal-Architecture)

기존 Layered Architecture의 경우 모든 계층이 영속성 계층을 토대로 만들어지기 때문에,

비즈니스 로직의 변경이 어렵고, 테스트 또한 영속성 컴포넌트에 대한 의존성이 생기기 때문에 테스트의 복잡도를 높입니다.

<br>

헥사고날 아키텍처는 이런 문제점을 **의존 역전**을 통해 의존성이 **도메인**을 향하게 하면서 이런 문제를 해결합니다.

어플리케이션의 핵심 로직을 외부 시스템으로부터 격리시켜 외부 **요소의 변화에 의해 핵심 로직이 영향을 받지 않도록 합니다.**

---

## Hexagon

구현되어 있는 각 Hexagon(Module)은 아래와 같이 사용됩니다.

### Domain Hexagon

도메인 모델을 정의하는 모듈로 어플리케이션의 핵심 로직을 담당합니다.

- 순수한 자바 객체(POJO)로 구현됩니다.
- Common 모듈 내의 라이브러리 외에 의존성을 가지지 않습니다.

<br>

### Use Case Hexagon

도메인에 대한 Use Case를 정의합니다.

- 외부 시스템과의 통신을 위한 Port 인터페이스 정의
- Domain 외 Spring Boot, Common 모듈 내 라이브러리 의존성을 가집니다.

<br>

### Infrastructure Hexagon

외부 인프라에 대한 의존성을 정의하는 모듈입니다.

- Domain, Use Case 외 Spring Boot, Common 모듈 내 라이브러리 의존성을 가집니다.
- 외부 인프라가 추가될떄 마다 Module을 분리해 관리합니다.
  - infrastructure/persistence
  - (필요 시 추가) infrastructure/kafka
  - (필요 시 추가) infrastructure/redis
- 각 모듈별로 config class를 정의하며, application-{module-name}.yml 파일을 통해 각 모듈별 설정을 관리합니다.

<br>

### Bootstrap Hexagon

여러 의존성들을 조합해 하나의 어플리케이션 서버를 구성하는 모듈입니다.

- 외부 요청을 받아 Use Case를 실행하기 위한 Primary Adapter를 정의합니다.
  - Rest Controller, Kafka Consumer 등
- Domain, Use Case, Infrastructure 외 Spring Boot, Common 모듈 내 라이브러리 의존성을 가집니다.
- Spring Boot Application을 정의합니다.
- Use Case와 Infrastructure를 의존합니ㅏㄷ.

---

## 구현 프로세스

### Component Scan에 Lazy Init 적용하기

Use Case 모듈의 UseCaseConfig에 Lazy Init 옵션을 적용합니다.

```kotlin
@Configuration
@ComponentScan(basePackages = ["com.usecase"], lazyInit = true)
class UseCaseConfig
```

적용 이유는 Bootstrap Module의 API 모듈은 Infrastructure Module의 Persistence Module에 대한 의존성이 존재하지 않습니다.

글서 DB에 저장하는 UseCase 클래스를 Component Scan으로 등록하면 빈 등록 에러가 발생합니다.

마찬가지로 Worker의 경우도 IntraStructure 내부의 모듈에 대한 의존성이 없기 때문에 빈 등록 에러가 발생합니다.

<br>

이 문제를 해결하기 위해 Usecase 모듈의 Component Scan에 Lazy Init 옵션을 주어 사용 시점에 Bean을 생성하도록 하였습니다.

이렇게 설정하면 API 에서 Usecase에 의존성을 갖는 클래스가 없게 되고, 마찬가지로 Worker도 동일하게 동작되기 때문에 Usecase 클래스를 Bean으로 등록하지 않게 됩니다.

<br>

Component Scan을 각 모듈내에서 수행되도록 하기 위해 각 모듈의 Component Scan Annotation의 패키지 위치를 잘 지정해야 합니다.

<br>

더 자세히 얘기하면 `@SpringBootApplication`이 `@ComponentScan`을 내장하고 있기 때문에, Application 클래스를 최상위로 끌어올리면,

`com.hexagonal-architecture.*` 패키지 하위의 모든 클래스들을 Component Scan 대상으로 두기 떄문에 의도치 않은 빈 등록 액션이 일어날 수 있습니다.

<br>

### Module 별 Config Class & Yaml 작성

각 모듈 별로 Config를 작성하고 Bootstrap Hexagon의 API, Worker 모듈의 Config는 각 모듈이 의존하는 Config를 Import 합니다.

```kotlin
@Configuration
@Import(
  value = [
    GrpcConfig::class,
    MongoConfig::class,
    UseCaseConfig::class
  ]
)
class ApiConfig 
```

<br>

```kotlin
@Configuration
@Import(
    PersistenceConfig::class,
    UseCaseConfig::class,
)
class WorkerConfig
```

<br>

**API Module의 Yaml**

하위 의존성에 대한 yaml을 inclide하도록 구현

```yaml
server:
  port: 9000
  shutdown: graceful
spring:
  profiles:
    active: local
    include:
      - grpc
      - mongo
      - usecase
```

<br>

**Worker Module의 Yaml**

하위 의존성에 대한 yaml을 inclide하도록 구현

```yaml
server:
  port: 9001
  shutdown: graceful
spring:
  profiles:
    active: local
    include:
      - persistence
      - usecase
```

---

## JPA Entity의 PK 생성 시 DB 채번(Auto-Increment)을 줄이기 위해 Ulid 사용

보통 JPA에서 Primary Key 생성 전략을 @GeneratedValue를 이용해 자동 생성할 수 있습니다. 

이런 전략을 사용하면 데이터베이스에서 자동으로 채번을 해주기 때문에 개발자는 신경쓰지 않아도 됩니다. 

하지만 이런 전략은 데이터 베이스에 대한 채번을 유발하며, 영속화 되기 전까진 id값을 null로 유지해야한다는, DB에 의존적인 코드를 작성하게 되는 단점이 있습니다.

<br>

이런 단점을 해결하기 위해 UUID를 사용하기도 합니다.. 

UUID는 DB 의존적이지 않고, 영속화 되기 전까지 id값을 null로 유지할 필요가 없습니다.

하지만 UUID는 생성 순서를 보장하지 않기 때문에 목록 조회 시 정렬기준으로 삼기에는 적합하지 않아 성능적인 이점을 가져갈 수 없습니다.

<br>

이때 ULID를 활용할 수 있습니다. 

ULID는 UUID와 호환성을 가지면서 시간순으로 정렬할 수 있는 특징을 가지고 있습니다. 

물론 ULID도단점이 있는데, UUID가 나노초까지 시간순을 보장해주는 반면 ULID는 밀리초까지만 시간순을 보장해줍니다. 

이를 보완하기위해 ULID Creator 라이브러리는 **Monotonic ULID**를 제공합니다. 

**Monotonic ULID**는 동일한 밀리초가 있다면 다음에 생성되는 ULID의 밀리초를 1 증가시켜서 생성하여 앞서 말한 단점을 보완합니다.

DB에 Primary Key를 채번하지 않고 도메인에서 직접 생성해서 사용하는 이러한 방식이 도메인이 외부에 의존하지 않고 직접 식별자를 생성할 수 있어서 클린 아키텍처에서는 더 큰 장점처럼 보였습니다.

<br>

**build.gradlew.kts**

```groovy
dependencies {
    implementation(libs.ulid.creator)
}
```

<br>

**UlidUtil.kt**

```kotlin
object UlidUtil {
    fun createUlid(): UUID {
        return UlidCreator.getMonotonicUlid().toUuid()
    }
}
```

<br>

**Post.kt**

```kotlin
data class Post(
    val id: UUID = UlidUtil.createUlid(),
    val title: String,
    val content: String
) {
    companion object {
        const val LIMIT_COUNT = 100L;
        fun create(title: String, content: String): Post {
            return Post(title = title, content = content)
        }
    }
}
```