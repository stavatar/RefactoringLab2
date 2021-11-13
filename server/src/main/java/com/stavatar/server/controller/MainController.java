package com.kekens.refactoring_lab_2_server.controller;

import com.kekens.refactoring_lab_2_server.exceptions.WrongDataException;
import com.kekens.refactoring_lab_2_server.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/calculator")
public class MainController {

    private final CalculatorService calculatorService;

    @Autowired
    public MainController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/help")
    public ResponseEntity<String> getHelpMessage() {
        return ResponseEntity.status(HttpStatus.OK).body(calculatorService.getHelpMessage());
    }

    @GetMapping("/step")
    public ResponseEntity<Double> getEvaluationStep(@RequestParam("step") int step, HttpSession httpSession) throws WrongDataException {
        List<Double> listStep = (List<Double>) httpSession.getAttribute("listStep");

        if (listStep != null) {
            double num = calculatorService.getEvaluationStep(listStep, step);
            calculatorService.saveDataInSession(httpSession, 0, num);
            return ResponseEntity.status(HttpStatus.OK).body(num);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/add")
    public ResponseEntity<Double> sub(@RequestParam("arg1") @NumberFormat(pattern = "#.####") double arg1,
                                             @RequestParam("arg2") @NumberFormat(pattern = "#.####") double arg2,
                                             HttpSession httpSession)
    {

        double result = calculatorService.addNumbers(arg1, arg2);
        calculatorService.saveDataInSession(httpSession, arg1, result);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/sub")
    public ResponseEntity<Double> subtraction(@RequestParam("arg1") @NumberFormat(pattern = "#.####") double arg1,
                                             @RequestParam("arg2") @NumberFormat(pattern = "#.####") double arg2,
                                             HttpSession httpSession)
    {
        double result = calculatorService.subNumbers(arg1, arg2);
        calculatorService.saveDataInSession(httpSession, arg1, result);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/mul")
    public ResponseEntity<Double> multiplication(@RequestParam("arg1") @NumberFormat(pattern = "#.####") double arg1,
                                             @RequestParam("arg2") @NumberFormat(pattern = "#.####") double arg2,
                                             HttpSession httpSession)
    {
        double result = calculatorService.mulNumbers(arg1, arg2);
        calculatorService.saveDataInSession(httpSession, arg1, result);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/div")
    public ResponseEntity<Double> division(@RequestParam("arg1") @NumberFormat(pattern = "#.####") double arg1,
                                             @RequestParam("arg2") @NumberFormat(pattern = "#.####") double arg2,
                                             HttpSession httpSession) throws WrongDataException
    {
        double result = calculatorService.divNumbers(arg1, arg2);
        calculatorService.saveDataInSession(httpSession, arg1, result);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/q")
    public void clearStep(HttpSession httpSession) {
        httpSession.removeAttribute("listStep");
    }


    @ExceptionHandler(WrongDataException.class)
    public ResponseEntity<String> handleException(WrongDataException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }
}
