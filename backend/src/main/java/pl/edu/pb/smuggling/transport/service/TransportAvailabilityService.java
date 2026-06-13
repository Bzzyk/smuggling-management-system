package pl.edu.pb.smuggling.transport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import pl.edu.pb.smuggling.cargo.repository.CargoRepository;
import pl.edu.pb.smuggling.transport.dto.AvailableCargoDto;
import pl.edu.pb.smuggling.transport.dto.AvailableSmugglerDto;
import pl.edu.pb.smuggling.transport.dto.AvailableVehicleDto;
import pl.edu.pb.smuggling.transport.model.Transport;
import pl.edu.pb.smuggling.transport.repository.TransportRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransportAvailabilityService {

    private final TransportRepository transportRepository;
    private final CargoRepository cargoRepository;
    private final JdbcTemplate jdbcTemplate;

    public Page<AvailableVehicleDto> getAssignableVehicles(Integer transportId, String vehicleType, int page, int size) {
        int requiredCapacity = Math.max(getCargoPackagesTotal(transportId), 1);
        Pageable pageable = PageRequest.of(page, size);
        List<Object> params = new ArrayList<>();
        params.add(requiredCapacity); 

        StringBuilder where = new StringBuilder(" WHERE load_capacity >= ?");
        if (vehicleType != null && !vehicleType.isBlank()) {
            where.append(" AND vehicle_type = ?");
            params.add(vehicleType);
        }

        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM v_available_vehicles" + where,
                Long.class,
                params.toArray()
        );

        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(pageable.getPageSize());
        queryParams.add(pageable.getOffset());

        List<AvailableVehicleDto> vehicles = jdbcTemplate.query(
                """
                SELECT vehicle_id, registration_number, brand, model, vehicle_type, load_capacity
                FROM v_available_vehicles
                """ + where + "\n" + """
                ORDER BY load_capacity ASC, registration_number ASC
                LIMIT ? OFFSET ?
                """,
                (rs, rowNum) -> new AvailableVehicleDto(
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("vehicle_type"),
                        rs.getInt("load_capacity")
                ),
                queryParams.toArray()
        );

        return new PageImpl<>(vehicles, pageable, total != null ? total : 0);
    }

    public Page<AvailableCargoDto> getAssignableCargos(Integer transportId, int page, int size) {
        Transport transport = getTransportById(transportId);
        Pageable pageable = PageRequest.of(page, size);
        List<Object> params = new ArrayList<>();
        params.add(transport.getOrder().getId());

        String where = " WHERE order_id IS NULL OR order_id = ?";
        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM v_available_cargo" + where,
                Long.class,
                params.toArray()
        );

        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(pageable.getPageSize());
        queryParams.add(pageable.getOffset());

        List<AvailableCargoDto> cargos = jdbcTemplate.query(
                """
                SELECT
                    cargo_id,
                    order_id,
                    name,
                    cargo_type,
                    packages_count,
                    estimated_value,
                    warehouse_id,
                    warehouse_name
                FROM v_available_cargo
                """ + where + "\n" + """
                ORDER BY name ASC, cargo_id ASC
                LIMIT ? OFFSET ?
                """,
                (rs, rowNum) -> new AvailableCargoDto(
                        rs.getInt("cargo_id"),
                        (Integer) rs.getObject("order_id"),
                        rs.getString("name"),
                        rs.getString("cargo_type"),
                        rs.getInt("packages_count"),
                        rs.getBigDecimal("estimated_value"),
                        (Integer) rs.getObject("warehouse_id"),
                        rs.getString("warehouse_name")
                ),
                queryParams.toArray()
        );

        return new PageImpl<>(cargos, pageable, total != null ? total : 0);
    }

    public Page<AvailableSmugglerDto> getAssignableSmugglers(String experienceLevel,
                                                             BigDecimal minSuccessRate,
                                                             int page,
                                                             int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Object> params = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE 1 = 1");

        if (experienceLevel != null && !experienceLevel.isBlank()) {
            where.append(" AND experience_level = ?");
            params.add(experienceLevel);
        }

        if (minSuccessRate != null) {
            where.append(" AND success_rate_percent IS NOT NULL AND success_rate_percent >= ?");
            params.add(minSuccessRate);
        }

        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM v_available_smugglers" + where,
                Long.class,
                params.toArray()
        );

        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(pageable.getPageSize());
        queryParams.add(pageable.getOffset());

        List<AvailableSmugglerDto> smugglers = jdbcTemplate.query(
                """
                SELECT
                    smuggler_id,
                    first_name,
                    last_name,
                    username,
                    experience_level,
                    completed_transports_count,
                    failed_transports_count,
                    success_rate_percent
                FROM v_available_smugglers
                """ + where + "\n" + """
                ORDER BY success_rate_percent DESC NULLS LAST, completed_transports_count DESC, last_name ASC, first_name ASC
                LIMIT ? OFFSET ?
                """,
                (rs, rowNum) -> new AvailableSmugglerDto(
                        rs.getInt("smuggler_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("experience_level"),
                        rs.getInt("completed_transports_count"),
                        rs.getInt("failed_transports_count"),
                        rs.getBigDecimal("success_rate_percent")
                ),
                queryParams.toArray()
        );

        return new PageImpl<>(smugglers, pageable, total != null ? total : 0);
    }

    public int getCargoPackagesTotal(Integer transportId) {
        Integer total = cargoRepository.sumPackagesByTransportId(transportId);
        return total != null ? total : 0;
    }

    private Transport getTransportById(Integer id) {
        return transportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono transportu o ID: " + id));
    }
}
