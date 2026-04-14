package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.repository.custom.CustomResLayerRepository;
import gov.moda.dw.manager.service.criteria.RelCriteria;
import gov.moda.dw.manager.service.dto.RelDTO;
import gov.moda.dw.manager.service.dto.custom.MenuTreeDTO;
import gov.moda.dw.manager.service.dto.custom.NodeDTO;
import gov.moda.dw.manager.type.RelType;
import gov.moda.dw.manager.type.ResType;
import gov.moda.dw.manager.type.StateType;
import gov.moda.dw.manager.util.LongFilterUtils;
import gov.moda.dw.manager.util.StringFilterUtils;
import java.sql.Connection;
import java.util.*;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class MenuTreeService {

    private final CustomResLayerRepository customResLayerRepository;
    private final CustomRelQueryService relQueryService;

    private final DataSource dataSource;

    public MenuTreeService(
        CustomResLayerRepository customResLayerRepository,
        CustomRelQueryService relQueryService,
        DataSource dataSource
    ) {
        this.customResLayerRepository = customResLayerRepository;
        this.relQueryService = relQueryService;
        this.dataSource = dataSource;
    }

    /**
     * 取得權限功能樹狀圖結構及已授權勾選內容
     * @param leftId
     * @param relType
     * @param resType
     * @return
     */
    public MenuTreeDTO readTree(Long leftId, RelType relType, ResType resType) {
        try (Connection conn = dataSource.getConnection()) {
            log.info("MenuTreeService-readTree 取得前端顯示授權內容開始 leftId={}, relType={}, resType={}", leftId, relType, resType);

            // 查詢使用哪個db
            // String dbName = dataSource.getConnection().getMetaData().getDatabaseProductName().toLowerCase();
            String dbName = conn.getMetaData().getDatabaseProductName().toLowerCase();

            List<Map<String, Object>> rowData = new ArrayList<>();
            if (dbName.contains("postgresql")) {
                // 查詢產樹節點順序
                rowData = customResLayerRepository.createPostgresqlTree(resType.getCode(), StateType.ENABLED.getCode());
            } else if (dbName.contains("microsoft sql server")) {
                // 查詢產樹節點順序
                rowData = customResLayerRepository.createMssqlTree(resType.getCode(), StateType.ENABLED.getCode());
            } else {
                throw new UnsupportedOperationException("未支援的資料庫 database: " + dbName);
            }

            // 依照id查詢所擁有的功能點
            RelCriteria relCriteria = new RelCriteria();
            relCriteria.setLeftTbl(StringFilterUtils.toEqualStringFilter(relType.getLeftTbl()));
            relCriteria.setLeftId(LongFilterUtils.toEqualLongFilter(leftId));
            List<RelDTO> relDTOList = relQueryService.findByCriteria(relCriteria);

            Map<String, NodeDTO> rootMap = new LinkedHashMap<>();
            Map<String, NodeDTO> intermediateMap = new HashMap<>();
            List<String> tickedNodes = new ArrayList<>();
            for (Map<String, Object> row : rowData) {
                String parentId = row.get("parent_id") == null ? "" : Long.toString((Long) row.get("parent_id"));
                String childId = ((Long) row.get("child_id")).toString();
                String childCode = Optional.ofNullable((String) row.get("child_code")).orElse("");
                String resName = Optional.ofNullable((String) row.get("res_name")).orElse("");
                String typeId = Optional.ofNullable((String) row.get("type_id")).orElse("");

                // 生成前端已賦予的功能(樹要顯示打勾的內容)
                for (RelDTO relDTO : relDTOList) {
                    if (relDTO.getRightId().toString().equals(childId)) {
                        tickedNodes.add(resName);
                    }
                }

                // 開始生成前端顯示的樹狀結構
                // 根節點生成
                NodeDTO rootNode = null;
                if (parentId.isEmpty() && ResType.Node.getCode().equals(typeId)) {
                    if (!rootMap.containsKey(childId)) {
                        NodeDTO newNode = new NodeDTO();
                        newNode.setLabel(resName);
                        newNode.setChildren(new ArrayList<>());

                        rootMap.put(childId, newNode);
                        continue;
                    }
                } else {
                    rootNode = rootMap.get(parentId);
                }

                // 中間節點生成
                if (!intermediateMap.containsKey(parentId) && ResType.Node.getCode().equals(typeId)) {
                    NodeDTO newNode = new NodeDTO();
                    newNode.setLabel(resName);
                    newNode.setChildren(new ArrayList<>());

                    intermediateMap.put(childId, newNode);
                    if (rootNode != null) {
                        rootNode.getChildren().add(newNode);
                    }
                } else if (ResType.Node.getCode().equals(typeId)) {
                    NodeDTO newNode = new NodeDTO();
                    newNode.setLabel(resName);
                    newNode.setChildren(new ArrayList<>());
                    intermediateMap.get(parentId).getChildren().add(newNode);
                    intermediateMap.put(childId, newNode);
                }

                // 尾端節點生成
                if (resType.getCode().equals(typeId)) {
                    NodeDTO endNode = new NodeDTO();
                    endNode.setLabel(resName);
                    endNode.setValue(childCode);
                    if (!intermediateMap.containsKey(parentId) && rootNode != null) {
                        rootNode.getChildren().add(endNode);
                    } else {
                        intermediateMap.get(parentId).getChildren().add(endNode);
                    }
                }
            }

            // 結構整理, 根節點與中間節點沒有尾端節點的,刪除掉, 前端不顯示沒有尾端節點的節點
            Iterator<Map.Entry<String, NodeDTO>> iterator = rootMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, NodeDTO> entry = iterator.next();
                if (this.checkNodeChildren(entry.getValue())) {
                    log.debug("MenuTreeService-checkNodeChildren 根節點 {} 沒有子節點, 移除掉.", entry.getValue().getLabel());
                    iterator.remove();
                }
            }

            //放入要回傳的資料
            MenuTreeDTO menuTreeDTO = new MenuTreeDTO();
            menuTreeDTO.setTickedNodes(tickedNodes);
            menuTreeDTO.setResNodeDTO(new ArrayList<>(rootMap.values()));
            log.info("MenuTreeService-readTree 取得前端顯示授權內容結束 leftId={}, relType={}, resType={}", leftId, relType, resType);

            return menuTreeDTO;
        } catch (Exception ex) {
            log.error("MenuTreeService-readTree 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 去除沒有尾端節點的節點
     * @param node
     * @return
     */
    private boolean checkNodeChildren(NodeDTO node) {
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            Iterator<NodeDTO> childIterator = node.getChildren().iterator();
            while (childIterator.hasNext()) {
                NodeDTO child = childIterator.next();
                if (this.checkNodeChildren(child)) {
                    log.debug("MenuTreeService-checkNodeChildren 節點 {} 沒有子節點, 移除掉.", node.getLabel());
                    childIterator.remove();
                }
            }
            return node.getChildren().isEmpty();
        } else {
            if (node.getValue() == null) {
                return true;
            } else {
                return false;
            }
        }
    }
}
