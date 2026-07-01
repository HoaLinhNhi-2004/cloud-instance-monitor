package com.techvalley.monitor.service;

import com.techvalley.monitor.domain.Client;
import com.techvalley.monitor.domain.Member;
import com.techvalley.monitor.domain.enums.MemberRole;
import com.techvalley.monitor.dto.ClientRequest;
import com.techvalley.monitor.dto.ClientResponse;
import com.techvalley.monitor.exception.ForbiddenException;
import com.techvalley.monitor.exception.ResourceNotFoundException;
import com.techvalley.monitor.repository.ClientRepository;
import com.techvalley.monitor.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final MemberRepository memberRepository;

    public List<ClientResponse> getAll(String currentEmail) {
        Member current = loadMember(currentEmail);
        List<Client> clients = (current.getRole() == MemberRole.ADMIN)
                ? clientRepository.findAll()
                : clientRepository.findAllByManagerId(current.getId());
        return clients.stream().map(ClientResponse::new).toList();
    }

    public ClientResponse getById(Long id, String currentEmail) {
        Client client = findOrThrow(id);
        checkAccess(client, currentEmail);
        return new ClientResponse(client);
    }

    public ClientResponse create(ClientRequest request) {
        Member manager = memberRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + request.getManagerId()));

        Client client = new Client();
        client.setClientName(request.getClientName());
        client.setContractPlan(request.getContractPlan());
        client.setManager(manager);
        return new ClientResponse(clientRepository.save(client));
    }

    public ClientResponse update(Long id, ClientRequest request) {
        Client client = findOrThrow(id);
        Member manager = memberRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + request.getManagerId()));

        client.setClientName(request.getClientName());
        client.setContractPlan(request.getContractPlan());
        client.setManager(manager);
        return new ClientResponse(clientRepository.save(client));
    }

    public void delete(Long id) {
        findOrThrow(id);
        clientRepository.deleteById(id);
    }

    private Client findOrThrow(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + id));
    }

    private Member loadMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + email));
    }

    private void checkAccess(Client client, String currentEmail) {
        Member current = loadMember(currentEmail);
        if (current.getRole() == MemberRole.CLIENT_MANAGER
                && !client.getManager().getId().equals(current.getId())) {
            throw new ForbiddenException("You do not have access to this client");
        }
    }
}
