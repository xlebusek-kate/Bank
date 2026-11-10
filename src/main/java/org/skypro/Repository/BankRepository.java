package org.skypro.Repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.skypro.UserInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
public class BankRepository {

    public final JdbcTemplate jdbcTemplate;

    private final Cache<UUID, Boolean> invest500Cache;
    private final Cache<UUID, Boolean> topSavingCache;
    private final Cache<UUID, Boolean> simpleCreditCache;

    public BankRepository(@Qualifier("BankJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.invest500Cache = Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(Duration.ofHours(1)).build();
        this.topSavingCache = Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(Duration.ofHours(1)).build();
        this.simpleCreditCache = Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(Duration.ofHours(1)).build();
    }

    public boolean isInvest500(UUID uuid) {
        return invest500Cache.get(uuid, key -> {
            Integer isDebit = Objects.requireNonNull(jdbcTemplate.queryForObject(
                    ("SELECT count(*) from TRANSACTIONS t" +
                            " INNER JOIN PRODUCTS p" +
                            " ON t.PRODUCT_ID = p.ID" +
                            " WHERE p.TYPE = 'DEBIT'" +
                            " AND t.USER_ID = ? "), Integer.class, key));

            Integer isInvest = Objects.requireNonNull(jdbcTemplate.queryForObject(
                    ("SELECT count(*) from TRANSACTIONS t" +
                            " INNER JOIN PRODUCTS p" +
                            " ON t.PRODUCT_ID = p.ID" +
                            " WHERE p.TYPE = 'INVEST'" +
                            " AND t.USER_ID = ? "), Integer.class, key));
            Integer sumSaving = Objects.requireNonNull(jdbcTemplate.queryForObject(
                    ("Select coalesce(sum(t.AMOUNT), 0) from TRANSACTIONS t " +
                            " INNER JOIN PRODUCTS p" +
                            " ON t.PRODUCT_ID = p.ID" +
                            " Where p.TYPE = 'SAVING'" +
                            " and t.Type ='DEPOSIT'" +
                            " AND t.USER_ID = ? "
                    ), Integer.class, key));

            return isDebit >= 1 && isInvest == 0 && sumSaving > 1000;
        });
    }


    public boolean isTopSaving(UUID uuid) {
        return topSavingCache.get(uuid, key -> {
            Integer isDebit = Objects.requireNonNull(jdbcTemplate.queryForObject(
                    ("SELECT count(*) from TRANSACTIONS t" +
                            " INNER JOIN PRODUCTS p" +
                            " ON t.PRODUCT_ID = p.ID" +
                            " WHERE p.TYPE = 'DEBIT'" +
                            " AND t.USER_ID = ? "), Integer.class, key));

            List<Integer> sums1 = jdbcTemplate.queryForList(
                    ("Select coalesce(sum(t.AMOUNT), 0) from TRANSACTIONS t " +
                            " INNER JOIN PRODUCTS p" +
                            " ON t.PRODUCT_ID = p.ID" +
                            " Where p.TYPE in( 'DEBIT' , 'SAVING')" +
                            " and t.TYPE = 'DEPOSIT' " +
                            " AND t.USER_ID = ? "
                    ), Integer.class, key);
            int max1 = sums1.stream().mapToInt(Integer::intValue).max().orElse(0);
            boolean sum1DebitOrSaving = max1 >= 50_000;

            Integer sums2 =
                    Objects.requireNonNull(jdbcTemplate.queryForObject(("Select coalesce(sum(case when t.TYPE = 'DEPOSIT' then t.AMOUNT else -t.AMOUNT end), 0)" +
                            " from TRANSACTIONS t" +
                            " inner join PRODUCTS p" +
                            " ON t.PRODUCT_ID = p.ID" +
                            " Where p.TYPE = 'DEBIT'" +
                            " and t.TYPE in ('DEPOSIT','WITHDRAW')" +
                            " and t.USER_ID = ? "), Integer.class, key));
            boolean sum2Diff = sums2 > 0;

            return isDebit >= 1 && sum1DebitOrSaving && sum2Diff;
        });
    }


    public boolean isSimpleCredit(UUID uuid) {
        return simpleCreditCache.get(uuid, key -> {
            Integer isCredit = Objects.requireNonNull(jdbcTemplate.queryForObject(
                    ("SELECT count(*) from TRANSACTIONS t" +
                            " INNER JOIN PRODUCTS p" +
                            " ON t.PRODUCT_ID = p.ID" +
                            " WHERE p.TYPE = 'CREDIT'" +
                            " AND t.USER_ID = ? "), Integer.class, key));
            Integer sums1 =
                    Objects.requireNonNull(jdbcTemplate.queryForObject(("Select coalesce(sum(case when t.TYPE = 'DEPOSIT' then t.AMOUNT else -t.AMOUNT end), 0)" +
                            " from TRANSACTIONS t" +
                            " inner join PRODUCTS p" +
                            " ON t.PRODUCT_ID = p.ID" +
                            " Where p.TYPE = 'DEBIT'" +
                            " and t.TYPE in ('DEPOSIT','WITHDRAW')" +
                            " and t.USER_ID = ? "), Integer.class, key));
            boolean sum1Diff = sums1 > 0;

            Integer sum2 = Objects.requireNonNull(jdbcTemplate.queryForObject(
                    ("SELECT coalesce(sum(t.AMOUNT), 0) from TRANSACTIONS t" +
                            " INNER JOIN PRODUCTS p" +
                            " ON t.PRODUCT_ID = p.ID" +
                            " WHERE p.TYPE = 'DEBIT'" +
                            " and t.TYPE = 'WITHDRAW'" +
                            " AND t.USER_ID = ? "), Integer.class, key));
            boolean sumWithCondition = sum2 > 100_000;

            return isCredit == 0 && sum1Diff && sumWithCondition;
        });
    }

    // НОВЫЙ МЕТОД ДЛЯ ОЧИСТКИ КЭША
    public void clearAllCaches() {
        invest500Cache.invalidateAll();
        topSavingCache.invalidateAll();
        simpleCreditCache.invalidateAll();
    }

    public List<UserInfo> findUsersByUsername(String username) {
        return jdbcTemplate.query(
                "select ID, FIRST_NAME, LAST_NAME from USERS where USERNAME = ?",
                (rs, i) -> new UserInfo(
                        UUID.fromString(rs.getString("ID")),
                        rs.getString("FIRST_NAME"),
                        rs.getString("LAST_NAME")),
                username);
    }
}