package pl.edu.pb.smuggling.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final JdbcTemplate jdbcTemplate;

    public List<ProfitReportRow> getProfitReport() {
        String sql = """
                SELECT
                    pr.order_id,
                    o.title AS order_title,
                    pr.total_revenue,
                    pr.total_costs,
                    pr.net_profit
                FROM v_profit_report pr
                JOIN orders o ON o.id = pr.order_id
                ORDER BY pr.net_profit DESC, o.title
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new ProfitReportRow(
                rs.getInt("order_id"),
                rs.getString("order_title"),
                rs.getBigDecimal("total_revenue"),
                rs.getBigDecimal("total_costs"),
                rs.getBigDecimal("net_profit")
        ));
    }

    public List<WarehouseStockReportRow> getWarehouseStockReport() {
        String sql = """
                SELECT
                    stock_id,
                    warehouse_name,
                    location,
                    cargo_name,
                    cargo_type,
                    quantity,
                    estimated_value,
                    added_at
                FROM v_warehouse_stock
                ORDER BY warehouse_name, cargo_name
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new WarehouseStockReportRow(
                rs.getInt("stock_id"),
                rs.getString("warehouse_name"),
                rs.getString("location"),
                rs.getString("cargo_name"),
                rs.getString("cargo_type"),
                rs.getInt("quantity"),
                rs.getBigDecimal("estimated_value"),
                toLocalDateTime(rs.getTimestamp("added_at"))
        ));
    }

    public List<RiskReportRow> getRiskReport() {
        String sql = """
                SELECT
                    at.transport_id,
                    at.order_title,
                    at.route_name,
                    at.start_point,
                    at.end_point,
                    at.distance_km,
                    at.difficulty_name,
                    at.risk_level,
                    at.transport_date,
                    calculate_transport_risk_score(at.transport_id) AS risk_score
                FROM v_active_transports at
                WHERE at.route_id IS NOT NULL
                ORDER BY risk_score DESC, at.transport_date
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new RiskReportRow(
                rs.getInt("transport_id"),
                rs.getString("order_title"),
                rs.getString("route_name"),
                rs.getString("start_point"),
                rs.getString("end_point"),
                rs.getBigDecimal("distance_km"),
                rs.getString("difficulty_name"),
                rs.getInt("risk_level"),
                toLocalDate(rs.getDate("transport_date")),
                rs.getBigDecimal("risk_score")
        ));
    }

    private LocalDate toLocalDate(Date date) {
        return date != null ? date.toLocalDate() : null;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    public record ProfitReportRow(
            Integer orderId,
            String orderTitle,
            BigDecimal totalRevenue,
            BigDecimal totalCosts,
            BigDecimal netProfit
    ) {
    }

    public record WarehouseStockReportRow(
            Integer stockId,
            String warehouseName,
            String location,
            String cargoName,
            String cargoType,
            Integer quantity,
            BigDecimal estimatedValue,
            LocalDateTime addedAt
    ) {
    }

    public record RiskReportRow(
            Integer transportId,
            String orderTitle,
            String routeName,
            String startPoint,
            String endPoint,
            BigDecimal distanceKm,
            String difficultyName,
            Integer riskLevel,
            LocalDate transportDate,
            BigDecimal riskScore
    ) {
    }
}
