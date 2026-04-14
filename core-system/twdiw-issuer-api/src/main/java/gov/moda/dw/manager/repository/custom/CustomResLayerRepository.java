package gov.moda.dw.manager.repository.custom;

import java.util.List;
import java.util.Map;
import gov.moda.dw.manager.repository.ResLayerRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface CustomResLayerRepository extends ResLayerRepository {
    // 提供檢查層數結構是否正確用(直接在SQL工具查詢檢查)
    //    "WITH cte_rel (PARENT_ID, PARENT_CODE, CHILD_ID, CHILD_CODE, lvl, odr, dn) AS (
    //    SELECT
    //    PARENT_ID,
    //    PARENT_CODE,
    //    CHILD_ID,
    //    CHILD_CODE,
    //    0,
    //    CAST(ISNULL(ORDERVAL, CHILD_CODE) AS VARCHAR(MAX)),
    //    CAST(CHILD_CODE AS VARCHAR(MAX))
    //    FROM RES_LAYER
    //    WHERE PARENT_ID IS NULL
    //    UNION ALL
    //    SELECT
    //    e.PARENT_ID,
    //    e.PARENT_CODE,
    //    e.CHILD_ID,
    //    e.CHILD_CODE,
    //    o.lvl + 1,
    //    CAST(o.odr AS VARCHAR(MAX)) + '.' + CAST(ISNULL(e.ORDERVAL, e.CHILD_CODE) AS VARCHAR(MAX)),
    //    CAST(o.dn AS VARCHAR(MAX)) + '.' + CAST(e.CHILD_CODE AS VARCHAR(MAX))
    //    FROM RES_LAYER e
    //    INNER JOIN cte_rel o ON o.CHILD_ID = e.PARENT_ID
    //)
    //    SELECT REPLICATE(' ', cte.lvl * 2) + cte.CHILD_CODE AS CODE, r.res_name, cte.CHILD_ID AS ID, cte.dn, cte.lvl, cte.odr FROM cte_rel AS cte
    //    JOIN res AS r ON cte.CHILD_CODE=r.res_id
    //    WHERE r.type_id='web' or r.type_id='node' 這邊換成自己要的查詢條件
    //    ORDER BY cte.odr"
    @Query(
        value = "WITH cte_rel (parent_id, parent_code, child_id, child_code, odr, dn) AS (\n" +
        "    SELECT\n" +
        "        parent_id,\n" +
        "        parent_code,\n" +
        "        child_id,\n" +
        "        child_code,\n" +
        "        CAST(ISNULL(ORDERVAL, CHILD_CODE) AS VARCHAR(MAX))," +
        "        CAST(child_code AS VARCHAR(MAX)) AS dn\n" +
        "    FROM RES_LAYER\n" +
        "    WHERE parent_id IS NULL\n" +
        "    UNION ALL\n" +
        "    SELECT\n" +
        "        e.parent_id,\n" +
        "        e.parent_code,\n" +
        "        e.child_id,\n" +
        "        e.child_code,\n" +
        "        CAST(o.odr AS VARCHAR(MAX)) + '.' + CAST(ISNULL(e.ORDERVAL, e.CHILD_CODE) AS VARCHAR(MAX))," +
        "        CAST(o.dn + '.' + e.child_code AS VARCHAR(MAX))\n" +
        "    FROM RES_LAYER e\n" +
        "    INNER JOIN cte_rel o ON o.child_id = e.parent_id\n" +
        ")\n" +
        "SELECT\n" +
        "    cte.parent_id,\n" +
        "    cte.child_id,\n" +
        "    cte.child_code,\n" +
        "    r.res_name,\n" +
        "    r.type_id,\n" +
        "    cte.dn\n" +
        "FROM cte_rel AS cte\n" +
        "JOIN res AS r ON cte.child_code = r.res_id\n" +
        "WHERE (r.type_id = :typeId OR r.type_id = 'node') and r.state = :state\n" +
        "ORDER BY cte.odr;",
        nativeQuery = true
    )
    List<Map<String, Object>> createMssqlTree(@Param("typeId") String typeId, @Param("state") String state);

    @Query(
        value = """
        WITH RECURSIVE cte_rel (parent_id, parent_code, child_id, child_code, odr, dn) AS (
                    SELECT
                        parent_id,
                        parent_code,
                        child_id,
                        child_code,
                        COALESCE(CAST(orderval AS TEXT), CAST(child_code AS TEXT)) AS odr,
                        CAST(child_code AS TEXT) AS dn
                    FROM res_layer
                    WHERE parent_id IS NULL
                    UNION ALL
                    SELECT
                        e.parent_id,
                        e.parent_code,
                        e.child_id,
                        e.child_code,
                        o.odr || '.' || COALESCE(CAST(e.orderval AS TEXT), CAST(e.child_code AS TEXT)) AS odr,
                        o.dn || '.' || CAST(e.child_code AS TEXT) AS dn
                    FROM res_layer e
                    INNER JOIN cte_rel o ON o.child_id = e.parent_id
                )
                SELECT
                    cte.parent_id,
                    cte.child_id,
                    cte.child_code,
                    r.res_name,
                    r.type_id,
                    cte.dn
                FROM cte_rel AS cte
                JOIN res AS r ON cte.child_code = r.res_id
                WHERE (r.type_id = :typeId OR r.type_id = 'node') AND r.state = :state
                ORDER BY cte.odr
        """,
        nativeQuery = true
    )
    List<Map<String, Object>> createPostgresqlTree(@Param("typeId") String typeId, @Param("state") String state);
}
