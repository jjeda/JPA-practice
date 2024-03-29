## 들어가며
- [김영한님의 JPA 강의](https://www.inflearn.com/course/ORM-JPA-Basic#)를 끝내보자
- 기간 : 8.14 ~ 8.22

## 계획
- 8.14 영속성 관리
- 8.15 엔티티 매핑
- 8.17 연관관계 매핑 기초
- 8.18 다양한 연관관계 매핑 & 고급 매핑

- 8.19 프록시와 연관관계 관리
- 8.20 값 타입
- 8.21 객체지향 쿼리 언어1
- 8.22 객체지향 쿼리 언어2

## JPA 기본
- JPA는 엔티티 매니저 팩토리라는 것을 만들어야 한다 // DB당 한개씩 묶임
- 엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체에서 공유
- 앤티티 매니저를 통해서 작업을 해야 한다
- 엔티티 매니저는 쓰레드간 절대 공유하면 안돼// 사용하고 버릴것
- 모든 데이터 변경은 트랜잭션 안에서 실행해야한다

#### JPA에서 가장 중요한 2가지
- 객체와 관계형 데이베이스 매핑하기
- 영속성 컨텍스트 // 실제 내부에서 어떻게 동작?  

## 1. 영속성 컨텍스트 // JPA를 이해하는데 가장 중요한 용어
- 엔티티를 영구 저장하는 환경
- EntityManager.persist(entity);
- DB에 저장하는 것이아니라 영속성 컨텍스트 라는곳(논리적인 개념)에 저장
- 엔티티 매니저를 통해서 영속성 컨텍스트에 접근
- 디비와 애플리케이션 사이에 중간 계층을 둠으로써 버퍼링, 캐싱 등의 이점을 가짐
- 성능보다는 매커니즘에서 얻을 수 있는 이점

#### 이점 1 : 1차 캐시
- DB에서 값을 가져오기전에 1차 캐시(영속성 컨텍스트)에 있는 것을 가져옴
- 만약 1차 캐시에없다면 DB에서 가져오면서 1차캐시에 저장하고 반환
- 영속엔티티의 동일성(identity)도 보장! //같은 트랜잭션안에서

#### 이점 2 : 트랜잭션을 지원하는 쓰기 지연 // 쓰기 지연 SQL 저장소
- em.persist(memberA)을 하면 DB가아니라 쓰기 지연 SQL 저장소에 저장
- commit()을 하는 순간 쓰기 지연 SQL 저장소에 저장돼있는 쿼리들이 날아감!
- jdbc.batch option 으로 한번의 network로 여러 쿼리를 보낼 수 있다!

#### 이점 3 : 엔티티 수정(Dirty checking) // 변경 감지
- 변경을 하고 persist를 호출해야 하는거아냐? 노노
- 자바 컬렉션 다루듯이 하자
- 이게 어떻게 가능하지?
  - (커밋할 떄) flush()가 호출되면서 1차 캐시 내에 엔티티와 스냅샷(최초 상태) 비교
  - update 쿼리를 쓰기 지연 SQL 저장소에 넣고
  - 마지막에 최종적으로 쿼리를 날림~
  
#### 이점 4 : 지연로딩(Lazy Loading)
  
#### 1-1. 플러시
- 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화 //커밋할때
- 1차 캐싱을 비우지 않는다 // 플러시 라는 이름에 헷갈리지 말
- **트랜잭션이라는 작업 단위가 중요 -> 커밋 직전에만 동기화 하면**
- 더티체킹, 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
- 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 등록
- 영속성 컨텍스트를 플러시하는 방법
  - em.flush() : 직접 호출
  - 트랜잭션 커밋 : 플러시 자동 호출
  - JPQL 쿼리 실행 : 플러시 자동 호출 
    - persist 후 커밋하지않고 중간에 JPQL 실행하게되면 문제가 생김
    - JPA는 이런 문제를 해결하고자 JPQL 실행할 때 플러시를 자동 호출
    - 옵션을 설정해서 자동호출 안할 수 있음~
    
 #### 1-2. 준영속 상태
 - 영속 상태의 엔티티가 영속성 컨텍스트(JPA가 관리하는 상태)에서 분리(detached)
 - 영속성 컨텍스트에서 분리됐기 때문에 제공하는 기능을 사용하지못해
 - 방법
   - em.detach(); // 특정 엔티티만
   - em.clear(); // 1차 캐시에 있는 엔티티 모두
   - em.close(); // 영속성 컨텍스를 종료
   
## 2. 엔티티 매핑
- 객체와 테이블 매핑 // @Entity, @Table
- 데이터베이스 스키마 자동 생성
- 필드와 컬럼 매핑 //Column
- 기본 키 매핑
- Enumerated(EnumType.ORIDNAL) 을 쓰면 안되는이유?
  - INTEGER 타입으로 순서가 들어가기 때문에
  - ENUM 타입이 추가되면 알 수가없다..

#### 2-1. 기본 키 매핑
- @GeneratedValue(strategy = GenerationType.X)
  - IDENTITY : 기본 키 생성을 데이터베이스에 위임 // auto_increment
  - SEQUENCE : 유일한 값을 순서대로 생성하는 데이터베이스 오브젝트
  - TABLE : 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내냄
- 권장하는 식별자 전략
  - 기본 키 제약 조건 : Not Null, unique, **변하면 안된다**
  - 미래까지 이 조건을 만족하는 자연키(email, 전화번호) 찾기 어렵다-> 대리키 사용
  - 주민등록번호도 적절하지 않음
  - LONG형 + 대체키 + 키 생성전략 사용
  
#### 2-2. IDENTITY 전략 - 특징
- 상황 : 내가 값을 넣지 않는다 (Null 이면 자동으로 값을 setting 해줌)
- 뭐가 문제? DB에 가봐야 ID값을 알 수 있다.
- 그런데 영속성 컨텍스트에서 관리 되려면 무조건 PK값이 있어야 함
- em.persist() 를 호출하자마자 바로 DB에 Insert 쿼리를 날림 // commit 시점이아니라
- 즉 모아서 INSERT 하는 것이 IDENTITY에서는 불가능 함

#### 2-3. SEQUENCE 전략 - 특징
- 마찬가지로 PK를 먼저 가져와야 해
- DB에 다음값을 받아와서 영속성 컨텍스트에 저장
- PK값만 받아오기 때문에 INSERT 쿼리는 안날아가~ // commit 시점에 INSERT
- 버퍼링 가능
- 성능상으로 그냥 INSERT 하면 되지않을까..?
  - allocationSize를 통해 미리 50개 size를 DB에 올려놓고 쓰면
  - next call을 매번 안해도 돼
  - **동시성 이슈없이 다양한 문제 해결 가능**
  - DB SEQ = 1 이네? 한번 더 호출 해서 DB SEQ = 51 미리 확보
  - DB SEQ = 1 | 1 //실제 사용 메모리
  - DB SEQ = 51 | 2
  - DB SEQ = 51 | 3 ... 
  - DB SEQ = 51 | 51 -> DB SEQ = 101
  
## 3. 연관관계 매핑 기초
- 객체의 참조와 테이블의 외래 키를 매핑
- '객체지향 설계의 목표는 자율적인 객체들의 협력 공동체를 만드는 것이다.' - 조영호
- 테이블은 외래 키로 조인을 사용해서 연관된 테이블을 찾고
- 객체는 참조를 사용해서 연관된 객체를 찾는다
- 패러다임의 불일치..

#### 3-1. 양방향 연관관계와 연관관계의 주인
- 객체(참조) 테이블(외래키 조인) 차이를 이해 해야해 그래야 연관관계의 주인을 이해할 수 있지 ㅎㅎ
- 객체는 가급적 단방향이 좋지만.. 서로 참조가 가능하지^^
- JPA의 멘붕 난이도 /* mappedBy */
  - 사실 객체의 연관관계에서 양방향 연관관계는 단방향이 두개 있는거야
  - 반대로 테이블은 연관관계 자체가 양방향 관계 1개이지 (foreign key 하나로 양쪽 다 알수있어)
  - 이 패러다임의 불일치
- 연관관계의 주인(Owner)
  - 아니 그러면 객체의 양방향 연관관계에서 누가 관계의 주인이야? // 둘이 다른 행위를 하면 어떡해?
  - 하나를 연관관계의 주인으로 지정하자
  - 주인만이 외래 키를 관리(등록, 수정)
  - 주인이 아닌쪽은 읽기만 가능
  - 주인은 mappedBy 속성 사용 X
  - 주인이 아니면 mappedBy 속성으로 주인 지정
  - 누구를 주인으로? 외래키가 있는 곳을 주인으로 정해라~  // DB 입장에서 외래키가 있는곳이 Many (N쪽이 무조건 주인)
  - 그래야 멤버를 바꾸면 멤버에 대한 업데이트 쿼리가 나가지~ // 팀에서 바꿨는데 멤버에 업데이트 쿼리나가면..? 
  - 그런데 객체지향 관점에서는 양쪽다 넣어주는 것이 맞음..
    - flush, clear 가 있으면 상관없는데 // 1차 캐시에만 있는 상태에서는 가져오지 못해~
    - 테스트케이스에서는 JPA 없이도 동작해야하기 때문
    - 헷갈릴 수 있기 때문에 연관관계 편의 메소드를 생성하자
    - 매핑시 무한 루프를 조심하자 // toString(), lombok, JSON 생성 라이브러리
    - JSON 생성 라이브러리 : 컨트롤러에서 절대로 엔티티를 반환하지 말라 // DTO를 만들자

## 4. 다양한 연관관계 매핑
- 연관관계 매핑시 고려사항 //사실 다대다는 실무에서 쓰면안되는 방식
  - 다중성 // 다대일, 일대다, 일대일, 다대다
  - 단방향, 양방향
  - 연관관계의 주인
  
#### 일대일 관계
- 외래 키에 데이터베이스 유니크 제약조건 추가
- 주 테이블에 외래 키 단방향 //다대일 단방향이랑 유사
  - 객체지향 개발자 선호
  - JPA 매핑 편리
  - 주 테이블만 조회해도 대상 테이블에 데이터가 있는지 확인 가능
  - 단점 : 값이 없으면 외래 키에 null 허용
- 대상 테이블에 외래 키 단방향
  - 전통적인 데이터베이스 개발자 선호
  - 주 테이블과 대상 테이블을 일대일에서 일대다 관계로 변경할 때 테이블 구조 유지
  - 단점1 : 주테이블에서 참조 해야하므로 양방향으로 만들어야한다.
  - 단점2 : 프록시 기능의 한계로 지연 로딩으로 설정해도 항상 즉시 로딩됨

#### 다대다 매핑의 한계
- 편리해 보이지만 실무에서 사용x
- 연결 테이블이 단순히 열결만 하고 끝나지 않음
- 주문시간, 수량 같은 데이터가 들어올 수 있음 

## 5. 상속관계 매핑
- RDB에서 논리적인 상속모델 세가지에 대해 JPA 모두 지원
- @Inheritance(strategy = InheritanceType.XXX)
- 조인 전략(JOINED) : 인서트가 2번 발생하겠네
- 단일 테이블 전략(SINGLE_TABLE) : 한 테이블로 다 합쳐버려 // 기본전략
- 구현 클래스마다 테이블 전략(TABLE_PER_CLASS) // 중복된 필드..
- @DiscriminatorColumn(name="DTYPE") - @DiscriminatorValue("XXX)
  - item(super)만 검색할 때 어떤 sub인지 모르니까 항상 넣어주자
  - 조인 전략은 어떻게 조인해서 쿼리날리면 되겠는데, 단일 테이블은 알 수가없다
  - 그래서 단일 테이블은 @ 안넣어주어도 DTYPE이 만들어짐
  - TABLE_PER_CLASS 에서는 의미가 없고

#### 조인 전략 장단점
- 장점
  - 데이터가 정규화 돼있고, 부모만으로 비즈니스 로직 해결
  - 외래 키 참조 무결성 제약조건 활용가능
  - 저장공간 효율화
- 단점
  - 조회시 조인을 많이 사용, 성능 저하 // 조회 쿼리가 복잡함
  - 데이터 저장시 INSERT SQL 2번
- 단점이 크리티컬 하지 않아 정석적인 전략

#### 단일 테이블 전략 장단점
- 장점
  - 조인이 필요 없으므로 일반적으로 조회 성능 빠름 //일반적인 경우
  - 조회 쿼리가 단순함
- 단점
  - 자식 엔티티가 매핑한 컬럼은 모두 null 허용 // critical
  - 테이블이 커질수 있어 조회 성능이 오히려 느려질 수 있음 // 특별한 경우
  
#### 구현 클래스마다 테이블 전략
- 은 쓰지말자^^
- 객체지향, RDB에 둘다 어울리지 않음
- 여러 자식 테이블을 함께 조회할 때 UNIOM 됨.. // critical

#### @MappedSuperclass
- 공통 매핑 정보가 필요할때 사용(id, name)
- DB는 다 따로 쓸건데
- 마치 method 화하듯이 // 귀찮은 행위를 대신 해주는~
- 상속관계 매핑x, 엔티티x, 테이블과 매핑x
- 자식클래스에 매핑 정보만 제공 
- 상속관계에서는 부모로 조회가 가능했는데 얘는 불가능
- 직접 생성할 일이 없으므로 추상클래스로 만들자

## 6. 프록시
- Member를 조회할 때 Team도 함께 조회해야 할까?
  - Member만 가져오거나 , Team도 함께 가져오고 싶은 다른 상황에 대응 할 수 있을까?
  - 지연로딩, 프록시가 해결~
- em.find() : 실제 엔티티 객체 조회
- em.getReference() : 프록시(가짜) 엔티티 객체 조회(디비에 쿼리가 안날아가는)
```java
class Proxy {
    Entity target; // getId(),getName() 호출되면 영속성 컨텍스트 통해서 초기화 요청을한다.
    getId() {}     // DB에 조회후 -> 실제 엔티티 생성 -> target 에 엔티티를 참조할 수 있게됨
    getName() {}   // 참조 후에는 Entity의 getId(), getName()을 대신 호출해준다 
}
```
- 프록시 특징
  - 실제 클래스를 상속 받아서 만들어짐 -> 겉 모양이 같다 // 타입 체크시 == 비교가 안되고 instance of 사용 해야한다
  - 사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지 않고 사용하면 됨
  - 처음 사용할 때 한 번만 초기화
  - 프록시 객체가 실제 엔티티로 바뀌는 것이 아님! 초기화되면 실제 엔티티에 접근 가능하게 되는거
  - 영속성 컨텍스트에 이미 존재하는데 프록시 객체를 만들면 실제 엔티티를 반환해줌 프록시가 아니라 
    - 이미 영속성 컨텍스트에 있는데 프록시로 만들어 얻는 이점이없다.
    - **JPA에서는 같은 트랜잭션안에서 항상 기존 엔티티와 proxy가 == true 여야 함**
    - 처음에 프록시를 만들고 em.find()를 호출하면? 
    - em.find() 호출 결과 엔티티가 아니라 프록시를 반환하여 == 을 성립하게 한다
    - 프록시든 아니든 개발에 문제없게 개발하자가 포인트
  - 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일때, 프록시를 초기화하면 문제발생
  - Hibernate.initialize(); 로 초기화 할 수있다.
  
#### 6.1 즉시 로딩과 지연 로딩
- 사실 프록시를 직접 쓸 일이없고..
- 즉시 로딩(멤버랑 팀이랑 거의 같이 부를거야~)
  - @ManyToOne(fetch = FetchType.EAGER)
  - 실무에서는 쓰지말자
    - 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생
    - 즉시 로딩은 JPQL에서 N(추가쿼리)+1(처음쿼리) 문제를 일으킨다
    - @ManyToOne, @OneToOne은 기본이 즉시 로딩 -> LAZY로 설정하자
    - @OneToMany, @ManyToMany는 기본이 지연 로딩
- 지연 로딩(프록시)
  - Team 객체에 @ManyToOne(fetch = FetchType.LAZY) : 프록시를 사용해서 멤버 객체만 DB에서 조회하겠다~
  - 그다음에 Team에 getName() 등으로 호출할 때 쿼리가 발생
  - 팀을 가져올때가아닌 팀을 사용할 때^^
- 결론 : 일단 무조건 LAZY로 설정하고
  1. JPQL FetchJoin 로 대부분 해결
  2. @EntityGraph
  3. Batch Size

#### 6.2 영속성 전이(CASCADE)
- 특정 엔티티를 영속 상태로 만들 때 다른 엔티티도 영속 상태로 만들기 위해
- 영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없음
- 연관된 엔티티도 함께 영속화하는 편리함을 제공할 뿐
- 라이프사이클을 동일하게 -> ALL, 삭제는 안되게.. -> PERSIST, 삭제만 같이 -> REMOVE
- 그럼 언제 쓸까? -> **단일 소유자**
  - 게시판의 첨부파일 같이 하나의 게시물에서만 관리되는 첨부파일들
  - 파일을 여러군데, 다른 엔티티에서 관리하는 경우는 쓰면안된다

#### 6.3 고아 객체 (orphanRemoval =true)
- 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제
- 컬렉션에서 빠진 자식 엔티티는 자동으로 DB에서 사라짐
- 참조하는 곳이 하나일 때 사용해야함! // 개인 소유일때
- @OneToMany 에서만 사용가능
- cascade 와 orpanRemoval을 동시에 사용하면
  - 생명주기를 부모가 관리하기 때문에 DAO 나 Repository 가 없어도!?
  - DDD의 Aggregate Root의 개념을 구현
  
## 7. 값 타입
- JPA의 데이터 타입 분류
  - 엔티티 타입 // @Entity
    - 데이터가 변해도 식별자로 지속해서 추적 가능
    - 회원 엔티티의 키가 나이 값을 변경해도 식별자로 인식 가능
  - 값 타입 // int, Integer, String 처럼 값으로 사용하는 자바 기본 타입 or 객체
    - 식별자가 없고 값만 있으므로 변경시 추적 불가
    - 기본값 타입
      - 자바 기본 타입(int, double)
      - 래퍼 클래스(Integer, Long)
      - String
    - 임베디드 타입(embedded type, 복합 값 타입)
      - x y 좌표 -> 커스텀 값을 사용하고싶다
    - 컬렉션 값 타입
    
#### 7.1 임베디드 값 타입
- int, String과 같은 값 타입
- 재사용, 높은 응집도
- Period.isWork()처럼 해당 값 타입만 사용하는 의미 있는 메소드를 만들 수 있음
- 엔티티에 생명주기를 의존
- 임베디드 타입은 엔티티의 값을 뿐이고 매핑하는 테이블은 같다
- 객체화 테이블을 아주 세밀하게 패밍하는 것이 가능
- 잘 설계한 ORM 애플리케이션은 매핑한 테이블의 수보다 클래스의 수가 더 많음
- equals, hascode를 구현할 때는 getter를 이용하자 // 프록시일 때 직접 접근 불가능 하기 때문
- 의미있는 비즈니스 메서드를 만들 수 있다!

#### 값타입은 공유참조를 피할 수 없는 한계
- 객체 타입을 수정할 수 없게 만들면 부작용을 원천 차단
- 불변객체
- Setter를 다없애자 (or private)
- 값을 바꾸고 싶을 때는 새로운 객체를 새로 만들면 되겠네
- +값 타입을 비교할 때는 equals() 메소드를 적절하게 재정의

#### 7.2 값 타입 컬렉션
- 값 타입을 컬렉션에 담아서 쓰는 것
- RDB 는 컬렉션을 넣어줄 수 없는 구조 -> 별도의 일대다 테이블을 구성해야 함
- 값 타입 컬렉션은 cascade, 고아 객체 제거기능을 필수로 가진다고 볼 수 있다.
- 식별자 개념이 없다.. -> 변경하면 추적이 어려움
- 값 타입 컬렉션에 변경 사항이 발생하면 , 주인 엔티티와 연관된 모든 데이터를 삭제하고 현재 값을 다시 모두 저장
- ...critcal
- odercolum 넣어서 하면 될거같은데?? 여기에도 많은 문제가 있다.. 
- 매핑하는 테이블은 모든 컬럼을 묶어서 PK값을 구성해야 함 // Null x & 중복 저장 x
- 결론 : 쓰면 안되겠네^^ -> Entity로 한번 감싸주자 // Adress 라는 임베디드 값 타입이 있으면 AdressEntity를 하나 만들어 
- 이 경우에만 예외적으로 일대다 단방향으로 설계하자 // cascade.ALL + oppanremoval  
- 값 타입 컬렉션은 그럼 언제..? // 진짜 단순할 때 [ 치킨 , 피자 ] 셀렉트 박스

## 8. JPQL
- 만약에 나이가 18살 이상인 회원을 모두 가져오고 싶다면?
- JPA를 사용하면 엔티티 객체를 중심으로 개발하는데..
- 문제는 검색 쿼리
- 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로..
- 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요
- **엔티티 객체를 대상으로 쿼리**


## 0. Question
- 백선장님 JPA 프로그래밍 5.엔티티 상태와 Cascade
  - cascade 예제에서 @ManyToOne 설정을 해서 Owner가 됐다.
  - Owner에서 set하고 save했는데 왜 One부분에 테이블이 안올라갔지?
    - -> 자세히 보니 post(부모) 만 저장을 했네..
    - -> 그래서 이 라이프사이클을 자식이자 owner인 comment 와 함께하기위해 cascade를 쓴 것
  - CASCADE.ALL 을 쓰면 어짜피 부모객체 지웠을 때 자식 객체 지워질텐데 orpanremoval이 필요한이유가 뭘까?
    - -> 부모객체를 삭제하는 상황이 아니라 자식노드만 부모 객체에서 떼어내는 경우에
    - -> 부모 테이블에서 삭제는 됐지만 아직 자식객체 테이블에는 남아있겠지?
    - -> 이런상황에서 부모가 없어진 고아객체는 자동으로 DB에서 사라지게 하는 것이 oropanremoval = true
    - -> repository, DAO 없이 자식의 생명주기를 부모가 관리할 수있다.