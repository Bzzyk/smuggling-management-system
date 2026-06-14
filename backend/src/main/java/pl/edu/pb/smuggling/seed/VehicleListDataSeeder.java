package pl.edu.pb.smuggling.seed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.transport.dto.VehicleFormDto;
import pl.edu.pb.smuggling.transport.model.VehicleType;
import pl.edu.pb.smuggling.transport.repository.VehicleRepository;
import pl.edu.pb.smuggling.transport.service.VehicleService;

import java.util.Random;

@Component
@Profile("vehicle-seed")
public class VehicleListDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(VehicleListDataSeeder.class);

    private static final int VEHICLE_COUNT = 1000;

    private final Random random = new Random(30);

    private final VehicleRepository vehicleRepository;
    private final VehicleService vehicleService;

    public VehicleListDataSeeder(
            VehicleRepository vehicleRepository,
            VehicleService vehicleService
    ) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleService = vehicleService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (vehicleRepository.existsByRegistrationNumber("BI10001")) {
            log.info("Vehicle sample data already exists, skipping seeding.");
            return;
        }

        int vehicles = createVehicles();

        log.info("Seeded vehicle sample data: {} vehicles.", vehicles);
    }

    private int createVehicles() {
        VehicleSeed[] seeds = {
                new VehicleSeed("Volkswagen", "Transporter", VehicleType.VAN, 900, 1600),
                new VehicleSeed("Ford", "Transit", VehicleType.VAN, 900, 1700),
                new VehicleSeed("Mercedes", "Sprinter", VehicleType.BUS, 1200, 2300),
                new VehicleSeed("Iveco", "Daily", VehicleType.CIEZAROWKA, 2500, 7000),
                new VehicleSeed("Renault", "Master", VehicleType.VAN, 1000, 1800),
                new VehicleSeed("Fiat", "Ducato", VehicleType.VAN, 1000, 1800),
                new VehicleSeed("Skoda", "Octavia", VehicleType.SAMOCHOD_OSOBOWY, 300, 650),
                new VehicleSeed("Toyota", "Corolla", VehicleType.SAMOCHOD_OSOBOWY, 300, 600)
        };
        String[] prefixes = {"BI", "WA", "KR", "GD", "LU", "PO", "WR", "SZ", "OL", "RZ"};
        int created = 0;

        for (int i = 1; i <= VEHICLE_COUNT; i++) {
            VehicleSeed seed = seeds[(i - 1) % seeds.length];
            String registration = prefixes[(i - 1) % prefixes.length] + String.format("%05d", 10000 + i);
            if (vehicleRepository.existsByRegistrationNumber(registration)) {
                continue;
            }

            VehicleFormDto dto = new VehicleFormDto();
            dto.setRegistrationNumber(registration);
            dto.setBrand(seed.brand());
            dto.setModel(seed.model());
            dto.setVehicleType(seed.type());
            dto.setLoadCapacity(randomInt(seed.minCapacity(), seed.maxCapacity()));
            dto.setAvailable(true);

            vehicleService.createVehicle(dto);
            created++;
        }

        return created;
    }

    private int randomInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private record VehicleSeed(
            String brand,
            String model,
            VehicleType type,
            int minCapacity,
            int maxCapacity
    ) {
    }
}
