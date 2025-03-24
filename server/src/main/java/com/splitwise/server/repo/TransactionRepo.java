package com.splitwise.server.repo;

import com.splitwise.server.model.Transaction;
import com.splitwise.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findByPayerOrPayee(User user, User user1);

    List<Transaction> findByPayeeIdOrPayerId(Long userId, Long userId1);

    @Query("SELECT t.payer.id, t.payer.name, SUM(t.amount) " +
            "FROM Transaction t WHERE t.payee.id = :userId AND t.group.id = :groupId AND t.settled = false " +
            "GROUP BY t.payer.id, t.payer.name")
    List<Object[]> findDebtsByUser(@Param("userId") Long userId, @Param("groupId") Long groupId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.payer.id = :userId AND t.group.id = :groupId AND t.settled = false")
    BigDecimal getTotalDebtForUserInGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);

    @Query("""
    SELECT u.id, u.name, COALESCE(SUM(t.amount), 0)\s
    FROM User u
    LEFT JOIN Transaction t ON u.id = t.payee.id AND t.group.id = :groupId AND t.settled = false
    WHERE u.id IN (
        SELECT ug.user.id FROM UserGroup ug WHERE ug.group.id = :groupId
    )
    GROUP BY u.id, u.name
""")
    List<Object[]> getTotalOwedPerUser(@Param("groupId") Long groupId);

    @Query("""
        SELECT SUM(t.amount), t.payer.id, u.name
        FROM Transaction t
        JOIN User u ON t.payer.id = u.id
        WHERE t.payee.id = :userId AND t.group.id = :groupId AND t.settled = false
        GROUP BY t.payer.id, u.name
    """)
    List<Object[]> getOweDetails(@Param("userId") Long userId, @Param("groupId") Long groupId);
}
