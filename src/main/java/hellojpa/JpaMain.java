package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        //Factory는 하나만 만들어야한다
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        //어떤 일괄적인 처리를 할때마다 EntityManager를 만들어야 한다
        //예) 고객이 들어와서 상품을 보고 장바구니에 담는다
        //자바의 컬렉션으로 이해하
        EntityManager em = emf.createEntityManager();
        //persistence.xml 에 설정한 UnitName "hello"로 EntityManagerFactory 와 EntityManager를 생성한다

        /*JPA의 모든 동작은 Transaction 안에서만 동작한다*/
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            /*CREATE*/
//            Member member = new Member(); //객체를 생성하고
//            member.setId(1L); //PK는 반드시 설정하고
//            member.setName("Hello");
//            em.persist(member); // EntityMager.persist() 를 통해 save한다

            /*READ*/
//            Member findMember = em.find(Member.class, 1L);
//            System.out.println("findMember = " + findMember.getName());

            /*DELETE*/
//            Member findMember = em.find(Member.class, 1L);
//            em.remove(findMember);

            /*UPDATE*/
            Member findMember = em.find(Member.class, 1L);
            findMember.setName("HelloJPA");
            //다시 저장을 하지 않는다????????????
            //JPA를 통해서 가져오면 관리가 시작함
            //commit하기전에 바뀐값이 있으면? UPDATE 쿼리를 날린다~


            /*JPQL : 객체지향 쿼리*/
            //검색을 할 때 모든 것을 객체로 변환해서 검색하는 것은 불가능
            //결국은 검색 조건이 포함된 쿼리가 필요해
            //테이블에서 가져오면 사상이 깨지잖아 그러니 객체를 대상으로~
            //왜냐하면 RDB를 대상으로 쿼리를 날리면 테이블에 종속돼버리는 코드가 되겠지?
            //dialect를 바꾸거나, Pagenation 등의 메리트가 있다
            List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .getResultList();
            //차이점 : 코드를 짤 때 테이블을 대상으로 짜는 것이아니라 객체를 대상으로~
            for (Member member : result) {
                System.out.println("memberName =" + member.getName());
            }
            
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback(); // 문제가 생겼을 경우 commit 이되면 안되므로 rollback 처리를 위한 try-catch
        } finally {
            em.close();
        }

        emf.close();
    }
}
