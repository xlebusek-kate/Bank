package org.skypro.Repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
public class BankRepository {

    public final JdbcTemplate jdbcTemplate;

    public BankRepository(@Qualifier("BankJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isInvest500(UUID uuid) {
        Integer isDebit = Objects.requireNonNull(jdbcTemplate.queryForObject(
                ("SELECT count(*) from TRANSACTIONS t" +
                        " INNER JOIN PRODUCTS p" +
                        " ON t.PRODUCT_ID = p.ID" +
                        " WHERE p.TYPE = 'DEBIT'" +
                        " AND t.USER_ID = ? "), Integer.class, uuid));

        Integer isInvest = Objects.requireNonNull(jdbcTemplate.queryForObject(
                ("SELECT count(*) from TRANSACTIONS t" +
                        " INNER JOIN PRODUCTS p" +
                        " ON t.PRODUCT_ID = p.ID" +
                        " WHERE p.TYPE = 'INVEST'" +
                        " AND t.USER_ID = ? "), Integer.class, uuid));
        Integer sumSaving = Objects.requireNonNull(jdbcTemplate.queryForObject(
                ("Select coalesce(sum(t.AMOUNT), 0) from TRANSACTIONS t " +
                        " INNER JOIN PRODUCTS p" +
                        " ON t.PRODUCT_ID = p.ID" +
                        " Where p.TYPE = 'SAVING'" +
                        " and t.Type ='DEPOSIT'" +
                        " AND t.USER_ID = ? "
                ), Integer.class, uuid));

        return isDebit >= 1 && isInvest == 0 && sumSaving > 1000;
    }


    public boolean isTopSaving(UUID uuid) {
        Integer isDebit = Objects.requireNonNull(jdbcTemplate.queryForObject(
                ("SELECT count(*) from TRANSACTIONS t" +
                        " INNER JOIN PRODUCTS p" +
                        " ON t.PRODUCT_ID = p.ID" +
                        " WHERE p.TYPE = 'DEBIT'" +
                        " AND t.USER_ID = ? "), Integer.class, uuid));

        List<Integer> sums1 = jdbcTemplate.queryForList(
                ("Select coalesce(sum(t.AMOUNT), 0) from TRANSACTIONS t " +
                        " INNER JOIN PRODUCTS p" +
                        " ON t.PRODUCT_ID = p.ID" +
                        " Where p.TYPE in( 'DEBIT' , 'SAVING')" +
                        " and t.TYPE = 'DEPOSIT' " +
                        " AND t.USER_ID = ? "
                ), Integer.class, uuid);
        int max1 = sums1.stream().mapToInt(Integer::intValue).max().orElse(0);
        boolean sum1DebitOrSaving = max1 >= 50_000;

        Integer sums2 =
                Objects.requireNonNull(jdbcTemplate.queryForObject(("Select coalesce(sum(case when t.TYPE = 'DEPOSIT' then t.AMOUNT else -t.AMOUNT end), 0)" +
                        " from TRANSACTIONS t" +
                        " inner join PRODUCTS p" +
                        " ON t.PRODUCT_ID = p.ID" +
                        " Where p.TYPE = 'DEBIT'" +
                        " and t.TYPE in ('DEPOSIT','WITHDRAW')" +
                        " and t.USER_ID = ? "), Integer.class, uuid));
        boolean sum2Diff = sums2 > 0;
        return isDebit >= 1 && sum1DebitOrSaving && sum2Diff;
    }


    public boolean isSimpleCredit(UUID uuid) {
        Integer isCredit = Objects.requireNonNull(jdbcTemplate.queryForObject(
                ("SELECT count(*) from TRANSACTIONS t" +
                        " INNER JOIN PRODUCTS p" +
                        " ON t.PRODUCT_ID = p.ID" +
                        " WHERE p.TYPE = 'CREDIT'" +
                        " AND t.USER_ID = ? "), Integer.class, uuid));
        Integer sums1 =
                Objects.requireNonNull(jdbcTemplate.queryForObject(("Select coalesce(sum(case when t.TYPE = 'DEPOSIT' then t.AMOUNT else -t.AMOUNT end), 0)" +
                        " from TRANSACTIONS t" +
                        " inner join PRODUCTS p" +
                        " ON t.PRODUCT_ID = p.ID" +
                        " Where p.TYPE = 'DEBIT'" +
                        " and t.TYPE in ('DEPOSIT','WITHDRAW')" +
                        " and t.USER_ID = ? "), Integer.class, uuid));
        boolean sum1Diff = sums1 > 0;

        Integer sum2 = Objects.requireNonNull(jdbcTemplate.queryForObject(
                ("SELECT coalesce(sum(t.AMOUNT), 0) from TRANSACTIONS t" +
                        " INNER JOIN PRODUCTS p" +
                        " ON t.PRODUCT_ID = p.ID" +
                        " WHERE p.TYPE = 'DEBIT'" +
                        " and t.TYPE = 'WITHDRAW'" +
                        " AND t.USER_ID = ? "), Integer.class, uuid));
        boolean sumWithCondition = sum2 > 100_000;

        return isCredit==0 && sum1Diff && sumWithCondition;
    }

}




