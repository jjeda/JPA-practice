package jpashop;

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
            tx.commit(); //이 시점에서 DB에 쿼리가 날아감
        } catch (Exception e) {
            tx.rollback(); // 문제가 생겼을 경우 commit 이되면 안되므로 rollback 처리를 위한 try-catch
        } finally {
            em.close();
        }
        emf.close();
    }
}
