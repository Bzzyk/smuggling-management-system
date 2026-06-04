package pl.edu.pb.smuggling.seed;

import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.order.model.OrderStatus;
import pl.edu.pb.smuggling.order.model.SmugglingOrder;
import pl.edu.pb.smuggling.order.repository.OrderStatusRepository;
import pl.edu.pb.smuggling.order.repository.SmugglingOrderRepository;
import pl.edu.pb.smuggling.transport.model.Route;
import pl.edu.pb.smuggling.transport.model.RouteDifficultyLevel;
import pl.edu.pb.smuggling.transport.model.SmugglerAssignment;
import pl.edu.pb.smuggling.transport.model.Transport;
import pl.edu.pb.smuggling.transport.model.TransportStatus;
import pl.edu.pb.smuggling.transport.model.Vehicle;
import pl.edu.pb.smuggling.transport.repository.RouteDifficultyLevelRepository;
import pl.edu.pb.smuggling.transport.repository.RouteRepository;
import pl.edu.pb.smuggling.transport.repository.SmugglerAssignmentRepository;
import pl.edu.pb.smuggling.transport.repository.TransportRepository;
import pl.edu.pb.smuggling.transport.repository.TransportStatusRepository;
import pl.edu.pb.smuggling.transport.repository.VehicleRepository;
import pl.edu.pb.smuggling.user.model.Role;
import pl.edu.pb.smuggling.user.model.SmugglerProfile;
import pl.edu.pb.smuggling.user.model.User;
import pl.edu.pb.smuggling.user.repository.RoleRepository;
import pl.edu.pb.smuggling.user.repository.SmugglerProfileRepository;
import pl.edu.pb.smuggling.user.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
@Profile("seed")
public class SampleDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SampleDataSeeder.class);

    private final Faker faker = new Faker(Locale.of("pl"));
    private final Random random = new Random(30);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SmugglerProfileRepository smugglerProfileRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final SmugglingOrderRepository orderRepository;
    private final RouteDifficultyLevelRepository difficultyLevelRepository;
    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;
    private final TransportStatusRepository transportStatusRepository;
    private final TransportRepository transportRepository;
    private final SmugglerAssignmentRepository assignmentRepository;

    public SampleDataSeeder(
            UserRepository userRepository,
            RoleRepository roleRepository,
            SmugglerProfileRepository smugglerProfileRepository,
            OrderStatusRepository orderStatusRepository,
            SmugglingOrderRepository orderRepository,
            RouteDifficultyLevelRepository difficultyLevelRepository,
            RouteRepository routeRepository,
            VehicleRepository vehicleRepository,
            TransportStatusRepository transportStatusRepository,
            TransportRepository transportRepository,
            SmugglerAssignmentRepository assignmentRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.smugglerProfileRepository = smugglerProfileRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.orderRepository = orderRepository;
        this.difficultyLevelRepository = difficultyLevelRepository;
        this.routeRepository = routeRepository;
        this.vehicleRepository = vehicleRepository;
        this.transportStatusRepository = transportStatusRepository;
        this.transportRepository = transportRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByUsername("seed_boss").isPresent()) {
            log.info("Sample data already exists, skipping seeding.");
            return;
        }

        Role bossRole = requireRole("BOSS");
        Role smugglerRole = requireRole("SMUGGLER");
        OrderStatus newOrderStatus = requireOrderStatus("NOWE");
        OrderStatus inProgressOrderStatus = requireOrderStatus("W_TRAKCIE");
        TransportStatus plannedStatus = requireTransportStatus("ZAPLANOWANY");
        TransportStatus onRoadStatus = requireTransportStatus("W_DRODZE");

        User boss = createUser("seed_boss", bossRole);
        List<SmugglerProfile> smugglers = createSmugglers(smugglerRole);
        List<Route> routes = createRoutes();
        List<Vehicle> vehicles = createVehicles();
        List<SmugglingOrder> orders = createOrders(boss, newOrderStatus, inProgressOrderStatus);
        List<Transport> transports = createTransports(orders, routes, vehicles, plannedStatus, onRoadStatus);
        createAssignments(transports, smugglers);

        log.info(
                "Seeded sample data: {} smugglers, {} routes, {} vehicles, {} orders, {} transports.",
                smugglers.size(), routes.size(), vehicles.size(), orders.size(), transports.size()
        );
    }

    private User createUser(String username, Role role) {
        User user = new User();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setUsername(username);
        user.setPasswordHash("{noop}password");
        user.setEmail(username + "@example.test");
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    private List<SmugglerProfile> createSmugglers(Role smugglerRole) {
        List<SmugglerProfile> profiles = new ArrayList<>();
        String[] levels = {"JUNIOR", "REGULAR", "SENIOR", "EXPERT"};

        for (int i = 1; i <= 8; i++) {
            User user = createUser("seed_smuggler_" + i, smugglerRole);

            SmugglerProfile profile = new SmugglerProfile();
            profile.setUser(user);
            profile.setExperienceLevel(levels[random.nextInt(levels.length)]);
            profile.setCompletedTransportsCount(random.nextInt(30));
            profile.setFailedTransportsCount(random.nextInt(6));
            profile.setActive(true);
            profile.setNotes(faker.lorem().sentence(8));

            profiles.add(smugglerProfileRepository.save(profile));
        }

        return profiles;
    }

    private List<Route> createRoutes() {
        List<RouteSeed> routeSeeds = List.of(
                new RouteSeed("Bialystok - Warszawa", "Bialystok", "Warszawa", "STANDARDOWA", "198.00"),
                new RouteSeed("Bialystok - Lublin", "Bialystok", "Lublin", "TRUDNA", "252.00"),
                new RouteSeed("Warszawa - Poznan", "Warszawa", "Poznan", "STANDARDOWA", "310.00"),
                new RouteSeed("Gdansk - Warszawa", "Gdansk", "Warszawa", "TRUDNA", "340.00"),
                new RouteSeed("Krakow - Wroclaw", "Krakow", "Wroclaw", "BARDZO_TRUDNA", "270.00"),
                new RouteSeed("Lublin - Rzeszow", "Lublin", "Rzeszow", "LATWA", "170.00")
        );

        List<Route> routes = new ArrayList<>();

        for (RouteSeed seed : routeSeeds) {
            if (routeRepository.existsByName(seed.name())) {
                continue;
            }

            RouteDifficultyLevel difficulty = requireDifficulty(seed.difficultyName());

            Route route = new Route();
            route.setName(seed.name());
            route.setStartPoint(seed.startPoint());
            route.setEndPoint(seed.endPoint());
            route.setDistanceKm(new BigDecimal(seed.distanceKm()));
            route.setDifficultyLevel(difficulty);
            route.setDescription(faker.lorem().sentence(10));

            routes.add(routeRepository.save(route));
        }

        return routes;
    }

    private List<Vehicle> createVehicles() {
        List<VehicleSeed> vehicleSeeds = List.of(
                new VehicleSeed("BI12345", "Volkswagen", "Transporter", "VAN", 1200),
                new VehicleSeed("BI54321", "Ford", "Transit", "VAN", 1400),
                new VehicleSeed("WA24680", "Mercedes", "Sprinter", "BUS", 1600),
                new VehicleSeed("GD13579", "Iveco", "Daily", "CIEZAROWKA", 3500),
                new VehicleSeed("KR98765", "Skoda", "Octavia", "SAMOCHOD_OSOBOWY", 550)
        );

        List<Vehicle> vehicles = new ArrayList<>();

        for (VehicleSeed seed : vehicleSeeds) {
            if (vehicleRepository.existsByRegistrationNumber(seed.registrationNumber())) {
                continue;
            }

            Vehicle vehicle = new Vehicle();
            vehicle.setRegistrationNumber(seed.registrationNumber());
            vehicle.setBrand(seed.brand());
            vehicle.setModel(seed.model());
            vehicle.setVehicleType(pl.edu.pb.smuggling.transport.model.VehicleType.valueOf(seed.vehicleType()));
            vehicle.setLoadCapacity(seed.loadCapacity());
            vehicle.setAvailable(true);

            vehicles.add(vehicleRepository.save(vehicle));
        }

        return vehicles;
    }

    private List<SmugglingOrder> createOrders(User boss, OrderStatus newStatus, OrderStatus inProgressStatus) {
        List<SmugglingOrder> orders = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            SmugglingOrder order = new SmugglingOrder();
            order.setTitle("Zlecenie transportowe " + i);
            order.setDescription(faker.lorem().sentence(12));
            order.setPlannedDate(LocalDate.now().plusDays(i * 3L));
            order.setStatus(i % 2 == 0 ? inProgressStatus : newStatus);
            order.setCreatedBy(boss);
            order.setResponsibleUser(boss);
            order.setEstimatedProfit(BigDecimal.valueOf(5_000L + random.nextInt(30_000)));

            orders.add(orderRepository.save(order));
        }

        return orders;
    }

    private List<Transport> createTransports(
            List<SmugglingOrder> orders,
            List<Route> routes,
            List<Vehicle> vehicles,
            TransportStatus plannedStatus,
            TransportStatus onRoadStatus
    ) {
        List<Transport> transports = new ArrayList<>();

        for (int i = 0; i < orders.size(); i++) {
            Route route = routes.get(i % routes.size());

            Transport transport = new Transport();
            transport.setOrder(orders.get(i));
            transport.setRoute(route);
            transport.setVehicle(vehicles.get(i % vehicles.size()));
            transport.setStatus(i % 2 == 0 ? plannedStatus : onRoadStatus);
            transport.setStartLocation(route.getStartPoint());
            transport.setDestination(route.getEndPoint());
            transport.setTransportDate(LocalDate.now().plusDays(i + 1L));
            transport.setPlannedArrivalDate(LocalDate.now().plusDays(i + 2L));
            transport.setDescription(faker.lorem().sentence(10));

            transports.add(transportRepository.save(transport));
        }

        return transports;
    }

    private void createAssignments(List<Transport> transports, List<SmugglerProfile> smugglers) {
        for (int i = 0; i < transports.size(); i++) {
            SmugglerAssignment assignment = new SmugglerAssignment();
            assignment.setTransport(transports.get(i));
            assignment.setSmuggler(smugglers.get(i % smugglers.size()));
            assignment.setActive(true);
            assignment.setNote(faker.lorem().sentence(6));

            assignmentRepository.save(assignment);
        }
    }

    private Role requireRole(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Missing role dictionary value: " + name));
    }

    private OrderStatus requireOrderStatus(String name) {
        return orderStatusRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Missing order status dictionary value: " + name));
    }

    private TransportStatus requireTransportStatus(String name) {
        return transportStatusRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Missing transport status dictionary value: " + name));
    }

    private RouteDifficultyLevel requireDifficulty(String name) {
        return difficultyLevelRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Missing route difficulty dictionary value: " + name));
    }

    private record RouteSeed(
            String name,
            String startPoint,
            String endPoint,
            String difficultyName,
            String distanceKm
    ) {
    }

    private record VehicleSeed(
            String registrationNumber,
            String brand,
            String model,
            String vehicleType,
            int loadCapacity
    ) {
    }
}
