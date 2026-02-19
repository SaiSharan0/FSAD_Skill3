package klh.edu.skill3;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class App {

    public static void main(String[] args) {

        SessionFactory factory = new Configuration()
                .configure()
                .addAnnotatedClass(Product.class)
                .buildSessionFactory();

        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();

        // Task 2: Insert 5–8 Products
        session.persist(new Product(1,"Pen","Stationery",10,50));
        session.persist(new Product(2,"Notebook","Stationery",50,30));
        session.persist(new Product(3,"Mouse","Electronics",500,10));
        session.persist(new Product(4,"Keyboard","Electronics",700,5));
        session.persist(new Product(5,"Bag","Accessories",1200,15));
        session.persist(new Product(6,"Bottle","Accessories",300,0));

        tx.commit();
        session.close();

        Session s = factory.openSession();

        // 3a: Sort by Price ASC
        System.out.println("\nPrice Ascending:");
        s.createQuery("FROM Product ORDER BY price ASC", Product.class)
                .list()
                .forEach(p -> System.out.println(p.getName()+" "+p.getPrice()));

        // 3b: Sort by Price DESC
        System.out.println("\nPrice Descending:");
        s.createQuery("FROM Product ORDER BY price DESC", Product.class)
                .list()
                .forEach(p -> System.out.println(p.getName()+" "+p.getPrice()));

        // 4: Sort by Quantity (Highest First)
        System.out.println("\nSort by Quantity DESC:");
        s.createQuery("FROM Product ORDER BY quantity DESC", Product.class)
                .list()
                .forEach(p -> System.out.println(p.getName()+" "+p.getQuantity()));

        // 5a: Pagination – First 3
        System.out.println("\nFirst 3 Products:");
        Query<Product> q1 = s.createQuery("FROM Product", Product.class);
        q1.setFirstResult(0);
        q1.setMaxResults(3);
        q1.list().forEach(p -> System.out.println(p.getName()));

        // 5b: Pagination – Next 3
        System.out.println("\nNext 3 Products:");
        Query<Product> q2 = s.createQuery("FROM Product", Product.class);
        q2.setFirstResult(3);
        q2.setMaxResults(3);
        q2.list().forEach(p -> System.out.println(p.getName()));

        // 6a: Count total products
        Long total = s.createQuery("SELECT COUNT(p) FROM Product p", Long.class)
                .uniqueResult();
        System.out.println("\nTotal Products: " + total);

        // 6b: Count products where quantity > 0
        Long qtyCount = s.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.quantity > 0",
                Long.class).uniqueResult();
        System.out.println("Products with quantity > 0: " + qtyCount);

        // 6c: Count grouped by description
        System.out.println("\nCount Grouped by Description:");
        List<Object[]> groupCount = s.createQuery(
                "SELECT p.description, COUNT(p) FROM Product p GROUP BY p.description",
                Object[].class).list();

        for(Object[] row : groupCount) {
            System.out.println(row[0] + " -> " + row[1]);
        }

        // 6d: Min and Max price
        Object[] minMax = s.createQuery(
                "SELECT MIN(p.price), MAX(p.price) FROM Product p",
                Object[].class).uniqueResult();

        System.out.println("\nMinimum Price: " + minMax[0]);
        System.out.println("Maximum Price: " + minMax[1]);

        // 7: GROUP BY description
        System.out.println("\nGROUP BY Description:");
        s.createQuery(
                "SELECT p.description, SUM(p.quantity) FROM Product p GROUP BY p.description",
                Object[].class)
         .list()
         .forEach(row -> System.out.println(row[0]+" -> "+row[1]));

        // 8: WHERE price range
        System.out.println("\nProducts with price between 100 and 800:");
        s.createQuery(
                "FROM Product p WHERE p.price BETWEEN 100 AND 800",
                Product.class)
         .list()
         .forEach(p -> System.out.println(p.getName()+" "+p.getPrice()));

        // 9a: Names starting with 'B'
        System.out.println("\nNames starting with B:");
        s.createQuery(
                "FROM Product p WHERE p.name LIKE 'B%'",
                Product.class)
         .list()
         .forEach(p -> System.out.println(p.getName()));

        // 9b: Names ending with 'e'
        System.out.println("\nNames ending with e:");
        s.createQuery(
                "FROM Product p WHERE p.name LIKE '%e'",
                Product.class)
         .list()
         .forEach(p -> System.out.println(p.getName()));

        // 9c: Names containing 'o'
        System.out.println("\nNames containing 'o':");
        s.createQuery(
                "FROM Product p WHERE p.name LIKE '%o%'",
                Product.class)
         .list()
         .forEach(p -> System.out.println(p.getName()));

        // 9d: Exact character length (length = 3)
        System.out.println("\nNames with exact length 3:");
        s.createQuery(
                "FROM Product p WHERE LENGTH(p.name) = 3",
                Product.class)
         .list()
         .forEach(p -> System.out.println(p.getName()));

        s.close();
        factory.close();
    }
}
