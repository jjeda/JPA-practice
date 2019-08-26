package jpashop;

import jpashop.domain.Member;
import jpashop.domain.Order;
import jpashop.domain.OrderItem;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            //Order order = em.find(Order.class, 1L);
            //Long memberId = order.getMemberId();

            /*데이터 중심 설계의 문제점*/
            //Member member = em.find(Member.class, memberId);
            // 객체 설계를 테이블 설계에 맞춘 방식
            // 테이블의 외래키를 객체에 그대로 가져옴
            // 객체 그래프 탐색이 불가능
            //Member findMember = order.getMember(); // 객체는 참조로 쭉쭉쭉 찾을 수 있어야 해
            //그래서 연관관계 매핑이란게 필요해~

           em.createQuery(
                   "select m From Member m where m.username like '%kim%'",
                   Member.class
           ).getResultList();


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
