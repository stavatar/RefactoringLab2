package com.kekens.refactoring_lab_2_server.service;

import com.kekens.refactoring_lab_2_server.exceptions.WrongDataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class CalculatorService {

    public double getEvaluationStep(final List<Double> listStep, final int step) throws WrongDataException
    {
        try {
            return listStep.get(step - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new WrongDataException(e.getMessage());
        }
    }

    public double addNumbers(double arg1, double arg2) {
        return arg1 + arg2;
    }

    public double subNumbers(double arg1, double arg2) {
        return arg1 - arg2;
    }

    public double mulNumbers(double arg1, double arg2) {
        return arg1 * arg2;
    }

    public double divNumbers(double arg1, double arg2) throws WrongDataException {
        if (arg2 == 0) {
            throw new WrongDataException("Cannot be divided by zero");
        } else {
            return arg1 / arg2;
        }
    }
    public void saveDataInSession(HttpSession httpSession, double arg1, double result) {
        List<Double> listStep = (List<Double>) httpSession.getAttribute("listStep");

        if (listStep == null) {
            listStep = new ArrayList<>(Arrays.asList(arg1, result));
        } else {
            listStep.add(result);
        }

        httpSession.setAttribute("listStep", listStep);
    }
    public String getHelpMessage() {
        return  "Usage:\n" +
                "when a first symbol on line is '>' – enter operand (number)\n" +
                "when a first symbol on line is '@' – enter operation\n" +
                "operation is one of '+', '-', '/', '*' or\n" +
                "'#' followed with number of evaluation step\n" +
                "'q' to exit";
    }

}
