/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.so.simulador;

import static br.usp.icmc.so.simulador.GlobalTime.getTime;
import static br.usp.icmc.so.simulador.GlobalTime.incrementTime;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author emanuel
 */
public class SchedulerRoundRobin extends Scheduler {

    private double quantum;
    public int n;

    public SchedulerRoundRobin(MainMemory mem, double _quantum) throws IOException {
        processes = new ArrayList<>();
        this.mem = mem;
        this.quantum = _quantum;
        
        //limpa o arquivo
        FileWriter fstream = new FileWriter("ReportRR.txt");
    }

    @Override
    public void addProcess(Process p) { //aloca o processo com a politica FIFO
        int pointer = mem.allocMemory(p.requiredMemory);
        p.setMemoryPointer(pointer);
        processes.add(p);
    }

    @Override
    public void execute() { //executa o primeiro da fila

        Iterator<Process> iterator = processes.iterator();
        Process p;

        while (!processes.isEmpty()) {
            while (iterator.hasNext()) {
                p = iterator.next();




                if (quantum > p.totalExecutionTime) {
                    incrementTime(p.totalExecutionTime);
                } else {
                    incrementTime((float) quantum);
                }

                

                p.setTotalExecutionTime(p.totalExecutionTime - (float) quantum);
                
                if (p.totalExecutionTime <= 0) {
                    writeReport(p);
                    System.out.println("Process " + p.getPid() + " leaving at " + getTime() + ".");
                    mem.freeMemory(p.getMemoryPointer(), p.getRequiredMemory());
                    iterator.remove();
                }



            }

            iterator = processes.iterator();
        }
    }

    public void writeReport(Process p) {
        try {
            // true -> concatena
            FileWriter fstream = new FileWriter("ReportRR.txt", true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("Process " + p.getPid() + " leaving at " + getTime() + ".\n");
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
