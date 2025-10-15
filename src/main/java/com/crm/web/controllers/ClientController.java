package com.crm.web.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.crm.web.models.Client;
import com.crm.web.models.ClientDto;
import com.crm.web.repositories.ClientRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clients")
public class ClientController {
  @Autowired
  private ClientRepository clientRepo;

  @GetMapping({ "", "/" })
  public String getClient(Model model) {
    var clients = clientRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
    model.addAttribute("clients", clients);
    return "clients/index";
  }

  @GetMapping("/create")
  public String createClient(Model model) {
    ClientDto clientDto = new ClientDto();
    model.addAttribute("clientDto", clientDto);

    return "clients/create";
  }

  @PostMapping("/create")
  public String createClientPost(@Valid @ModelAttribute ClientDto clientDto, BindingResult result) {
    if (clientRepo.findByEmail(clientDto.getEmail()) != null) {
      result.addError(new FieldError("clientDto", "email", clientDto.getEmail(), false, null, null,
          "Email address is already used"));
    }

    if (result.hasErrors()) {
      return "clients/create";
    }

    Client client = new Client();

    client.setFirstName(clientDto.getFirstName());
    client.setLastName(clientDto.getLastName());
    client.setEmail(clientDto.getEmail());
    client.setPhone(clientDto.getPhone());
    client.setAddress(clientDto.getAddress());
    client.setStatus(clientDto.getStatus());
    client.setCreatedAt(new Date().toString());

    clientRepo.save(client);

    return "redirect:/clients";
  }

  @GetMapping("/edit")
  public String editClient(Model model, @RequestParam int id) {
    Client client = clientRepo.findById(id).orElse(null);
    if (client == null) {
      return "redirect:/clients";
    }

    ClientDto clientDto = new ClientDto();
    clientDto.setFirstName(client.getFirstName());
    clientDto.setLastName(client.getLastName());
    clientDto.setEmail(client.getEmail());
    clientDto.setPhone(client.getPhone());
    clientDto.setAddress(client.getAddress());
    clientDto.setStatus(client.getStatus());

    model.addAttribute("clientDto", clientDto);
    model.addAttribute("client", client);

    return "clients/edit";
  }

  @PostMapping("/edit")
  public String editClientPost(
      @Valid @ModelAttribute("clientDto") ClientDto clientDto,
      BindingResult result,
      @RequestParam int id,
      Model model) {
    if (clientRepo.findByEmail(clientDto.getEmail()) != null) {
      result.addError(new FieldError("clientDto", "email", clientDto.getEmail(), false, null, null,
          "Email address is already used"));
    }

    if (result.hasErrors()) {
      // tetap kirim data id ke view, biar input hidden tetap ada
      model.addAttribute("client", clientRepo.findById(id).orElse(null));
      return "clients/edit";
    }

    var client = clientRepo.findById(id).orElse(null);
    if (client == null) {
      return "redirect:/clients";
    }

    client.setFirstName(clientDto.getFirstName());
    client.setLastName(clientDto.getLastName());
    client.setEmail(clientDto.getEmail());
    client.setPhone(clientDto.getPhone());
    client.setAddress(clientDto.getAddress());
    client.setStatus(clientDto.getStatus());

    clientRepo.save(client);

    return "redirect:/clients";
  }

  @GetMapping("/delete")
  public String deleteClient(@RequestParam int id) {
    Client client = clientRepo.findById(id).orElse(null);
    if (client != null) {
      clientRepo.delete(client);
    }

    return "redirect:/clients";
  }
}
