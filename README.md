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
  