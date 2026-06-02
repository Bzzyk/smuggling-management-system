package pl.edu.pb.smuggling.transport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pb.smuggling.transport.repository.TransportRepository;

@Service
@RequiredArgsConstructor
public class TransportService {
    private final TransportRepository transportRepository;
}
