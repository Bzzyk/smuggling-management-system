package pl.edu.pb.smuggling.transport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import pl.edu.pb.smuggling.cargo.repository.CargoRepository;
import pl.edu.pb.smuggling.transport.model.Transport;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransportEstimateService {

    private final CargoRepository cargoRepository;
    private final JdbcTemplate jdbcTemplate;

    public int getCargoPackagesTotal(Integer transportId) {
        Integer total = cargoRepository.sumPackagesByTransportId(transportId);
        return total != null ? total : 0;
    }

    public boolean isVehicleCapacityEnough(Transport transport) {
        if (transport.getVehicle() == null) {
            return false;
        }
        return transport.getVehicle().getLoadCapacity() >= getCargoPackagesTotal(transport.getId());
    }

    public TransportEstimate getTransportEstimate(Integer transportId) {
        BigDecimal riskScore = jdbcTemplate.queryForObject(
                "SELECT calculate_transport_risk_score(?)",
                BigDecimal.class,
                transportId
        );
        BigDecimal operationalCost = jdbcTemplate.queryForObject(
                "SELECT estimate_transport_operational_cost(?)",
                BigDecimal.class,
                transportId
        );
        BigDecimal cargoValue = jdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(estimated_value), 0)
                FROM cargo
                WHERE transport_id = ?
                """,
                BigDecimal.class,
                transportId
        );
        BigDecimal predictedProfit = jdbcTemplate.queryForObject(
                "SELECT calculate_transport_estimated_profit(?)",
                BigDecimal.class,
                transportId
        );

        return new TransportEstimate(riskScore, operationalCost, cargoValue, predictedProfit);
    }

    public record TransportEstimate(
            BigDecimal riskScore,
            BigDecimal operationalCost,
            BigDecimal cargoValue,
            BigDecimal predictedProfit
    ) {
    }
}
