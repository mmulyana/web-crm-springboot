package com.crm.web.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

  @RequestMapping("/error")
  public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
    Object statusCode = request.getAttribute("jakarta.servlet.error.status_code");
    int status = 500;
    String message = "Terjadi kesalahan pada server.";

    if (statusCode != null) {
      status = Integer.parseInt(statusCode.toString());
      switch (status) {
        case 403:
          message = "Kamu tidak punya akses ke halaman ini.";
          break;
        case 404:
          message = "Halaman tidak ditemukan.";
          break;
        case 400:
          message = "Permintaan tidak valid.";
          break;
        case 401:
          message = "Silakan login terlebih dahulu.";
          break;
        default:
          message = "Terjadi kesalahan yang tidak diketahui.";
      }
    }

    response.setStatus(status);

    model.addAttribute("status", status);
    model.addAttribute("message", message);

    return "error";
  }
}
