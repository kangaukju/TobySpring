> 의존관계(DI)
: 가장 중요한 개념은 제3자의 도움을 통해 두 오브젝트 사이의 유영한 관계가 설정되도록 만든다는 것

> 일정한 작업 흐름이 반복되면서 그중 일부 기능만 바뀌는 코드가 존재한다면 저략 패턴을 적용한다.
바꾸지 않는 부분은 컨텍스트, 바뀌는 부분은 전략으로 만들고 인터페이스를 통해 유연하게 전략을 변경할 수 있도록 구성한다.

> 컨텍스트가 하나 이상의 클라이언트 오브젝트에서 사용된다면 클래스를 분리해서 공유하도록 만든다.

>                          Excepttion
     [언체크 예외]                           [체크 예외]
    RuntimeException                       ....Exception
    ....Exception

>   체크 예외: 우리가 사용하는 일반적인 예외, 반드시 예외처리하지 않으면 컴파일 에러 (IOException, SQLException...)
> 언체크 예외: RuntimeException을 상속받은 예외, 예외처리 하지 않아도 됨 (NullPointerException, IllegalArgumentException...)

> DAO가 SQLException을 throw하는것은 바람직하지 못하다. DAO가 알아서 처리하도록... 또는 예외 전환을 사용한다.
  DuplicateUserIdException 또는 DuplicateKeyException으로 바꾸어 DAO가 throw한다.
  중첩예외(SQLException + DuplicateUserIdException)로 던지도록 한다.
  
> 어디에서든 Exception을 잡아서 처리할 수 있다면 굳이 체크 예외로 만들지 않고 런타임 예외로 만드는게 낫다.

> [예외 정리]
 - 예외를 잡아서 아무런 조취를 취하지 않거나 의미 없는 throws 선언을 남발하는 것은 위험하다.
 - 예외는 복구하거나 예외처리 오브젝트로 의도적으로 전달하거나 적절한 예외로 전환해야한다.
 - 좀 더 의미 있는 예외로 변경하거나, 불필요한 catch/throws를 피하기 위해 런타임 예외로 포장하는 두 가지 방법의 예외 전환이 있다.
 - 복구할 수 없는 예외는 가능한 한 빨리 런타임 예외로 전환하는 것이 바람직하다.
 - 애플리케이션의 로직을 담기 위한 예외는 체크 예외로 만든다.
 - JDBC의 SQLException은 대부분 복구할 수 없는 예외이므로 런타임 예외로 포장해야 한다.
 - SQLException의 에러코드는 DB에 종속되기 때문에 DB에 독립적인 예외로 전환될 필요가 있다.
 - 스프링은 DataAccessException을 통해 DB에 독립적으로 적용 가능한 추상화된 런타임 예외 계층을 제공한다.
 - DAO를 데이터 액세스 기술에서 독립시키려면 인터페이스 도입과 런타임 예외 전환, 기술에 독립적인 추상화된 예외로 전환이 필요하다.
  
> [트랜잭션]
 - 멀티스레드 환경에서 안전하지 않은 클래스를 빈으로 무작정 등록하면 심각한 문제가 발생한다.
 - 스프링이 제공하는 모든 PlatformTransactionManager의 구현 클래스는 싱글톤으로 사용이 가능하다.
 - PlatformTransactionManager 구현 클래스
``` 
 	* 하아버네이트: HibernateTransactionManager
 	* JPA: JPATransactionManager
 	* JDBC: JPATransactionManager
 ```
 
 > [서비스 추상화]
  - 비즈니스 로직을 담은 코드는 데이터 액세스 로직을 담은 코드와 깔끔하게 분리되는 것이 바람직한다.
    비즈니스 로직 코드 또한 내부적으로 책임과 역할에 따라서 깔끔하게 메소드로 정리돼야 한다.
  - 이를 위해서는 DAO의 기술 변화에 서비스 계층의 코드가 영향을 받지 않도록 인터페이스와 DI를 활용해서 결합도를 낮춰줘야 한다.
  - DAO를 사용하는 비즈니스 로직에는 단위 작업을 보장해주는 트랜잭션이 필요하다.
  - 트랜잭션 경계설정은 주로 비즈니스 로직 안에서 일어난다.
  - 트랜잭션 방법에 따라 비즈니스 로직을 담은 코드가 함께 변경되면 단일 책임 원칙에 위배되며, DAO가 사용하는 특정 기술에 대해 강한 결합을 만들어낸다.
  
> [AOP]
- 팩토리 빈이란? 스프링을 대신해서 오브젝트의 생성로직을 담당하도록 만들어진 특별한 빈을 말함.
```
package org.springframework.beans.factory;
public interface FactoryBean {
    public abstract Object getObject() throws Exception;
    public abstract Class getObjectType();
    public abstract boolean isSingleton();
}
``` 
 * 리플렉션은 private으로 선언된 접근 규약을 위반할 수 있는 강력한 기능을 가짐.(private 생성자를 가진 클래스도 오브젝트를 만들어준다.)
FactoryBean 인터페이스를 구현한 클래스를 스프링의 빈으로 등록하면 팩토린 빈으로 동작한다.
 * context.getBean(&[name of ID]) ?? 팩토리 빈이 만들어주는 빈 오브젝트가 아닌 팩토리 빈 자체를 가져온다 (빈 이름 앞에 '&' 사용)