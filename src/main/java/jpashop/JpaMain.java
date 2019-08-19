package jpashop;

import jpashop.domain.Member;
import jpashop.domain.Order;

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
            Order order = em.find(Order.class, 1L);
            Long memberId = order.getMemberId();

            //Member member = em.find(Member.class, memberId); //객체지향 스럽지못하다 -> 끊키게 돼
            Member findMember = order.getMember(); // 객체는 참조로 쭉쭉쭉 찾을 수 있어야 해
            //그래서 연관관계 매핑이란게 필요해~

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
