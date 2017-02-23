package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class MP02 {
    
    public static void main(String[] args){
        Machine machine1 = readFile("src/main/machine_a.txt");  
        Machine machine2 = readFile("src/main/machine_b.txt");  
        ArrayList<Partition> initial_partition = new ArrayList<Partition>();
        ArrayList<Partition> reduced_partition = new ArrayList<Partition>();
      
        if(baseCase(machine1, machine2)){
            Machine combined = combine(machine1, machine2);
            initial_partition = initialize(combined);
            reduced_partition = reduce(initial_partition, combined);
        
            if(isEqualMachines(machine1, machine2, reduced_partition)){
                System.out.println(machine1.getName() + " and " + machine2.getName() + " are equivalent!");   
            } else {
                System.out.println(machine1.getName() + " and " + machine2.getName() + " are NOT equivalent!");
            }
            
            displayTransitions(machine1, machine2, reduced_partition);
            
        } else {
            System.out.println(machine1.getName() + " and " + machine2.getName() + " are NOT equivalent!");
        }  
    }
    
    public static boolean isEqualMachines(Machine machine1, Machine machine2, ArrayList<Partition> partition){
        for(int i = 0; i < partition.size(); i++){
            if(partition.get(i).getStates().contains(machine1.getStart()) &&
               partition.get(i).getStates().contains(machine2.getStart())){
                    return true;
            }
        }
        return false;
    }
    
    public static Machine combine(Machine machine1, Machine machine2){        
        Machine combined = new Machine("combined");
        
        combined.addStates(machine1.getStates());
        combined.addStates(machine2.getStates());
        combined.setInputs(machine1.getInputs());
        combined.setOutputs(machine1.getInputs());
        combined.addTransTable(machine1.getTransTable());
        combined.addTransTable(machine2.getTransTable());       
         
        return combined;
    }
    
    public static boolean baseCase(Machine machine1, Machine machine2){
        
        if(machine1.getInputs().equals(machine2.getInputs()))
           if(machine1.getOutputs().equals(machine2.getOutputs()))
               return true;
        
        return false;
    }
    
    public static ArrayList<Partition> reduce(ArrayList<Partition> partitions, Machine machine){
        ArrayList<Partition> beforePart = new ArrayList<Partition>(partitions);
        ArrayList<Partition> afterPart = new ArrayList<Partition>();
        Partition temp;
        
        int cnt = 2;
        int blobLabel;
        boolean firstRun = true;

        while(!isEqualPartitions(beforePart, afterPart)) {
            //System.out.println("Partion #" + cnt);
            blobLabel = 0;
            cnt++;
            
            if(!firstRun){        
                beforePart = new ArrayList<Partition>(afterPart);          
                afterPart = new ArrayList<Partition>();
            }               
            
            for(int i = 0; i < beforePart.size(); i++){
                for(int j = 0; j < beforePart.get(i).getStates().size(); j++){
                    for(int k = 0; k < beforePart.get(i).getTransTable().size(); k++){
                        if(beforePart.get(i).getStates().get(j).equals(beforePart.get(i).getTransTable().get(k).getFromState())){
                            //System.out.println("in first if");
                            if(!checkIfExists(beforePart.get(i).getTransTable().get(k).getOutputs(), afterPart)){
                                //System.out.println("Create new blob");
                                temp = new Partition(beforePart.get(i).getTransTable().get(k).getOutputs(), String.valueOf(blobLabel));
                                temp.addState(beforePart.get(i).getStates().get(j));
                                afterPart.add(temp);
                                blobLabel++;
                            } else {
                                //System.out.println("Added to an existing blob");
                                for(int l = 0; l < afterPart.size(); l++){
                                    if(afterPart.get(l).getOutputs().equals(beforePart.get(i).getTransTable().get(k).getOutputs())){
                                        afterPart.get(l).addState(beforePart.get(i).getStates().get(j));
                                    }      
                                }
                            }                   
                        }
                    }
                } 
            }   
            
            setTransitions(afterPart, machine);
            //displayPartitions(afterPart);
            //displayTransitions(afterPart);
            firstRun = false;
        }       
        
        return beforePart;
    }
    
    public static boolean isEqualPartitions(ArrayList<Partition> beforePart, ArrayList<Partition> afterPart){
        int i, j, k;
        
        if(beforePart.size() == afterPart.size()){
            for(i = 0; i < beforePart.size(); i++){
                if(!beforePart.get(i).getLabel().equals(afterPart.get(i).getLabel())){
                    return false;
                }
                
                if(beforePart.get(i).getOutputs().size() == afterPart.get(i).getOutputs().size()){
                    for(j = 0; j < beforePart.get(i).getOutputs().size(); j++){
                        if(!beforePart.get(i).getOutputs().get(j).equals(afterPart.get(i).getOutputs().get(j)))
                            return false;
                    }
                } else return false;
                
                if(beforePart.get(i).getStates().size() == afterPart.get(i).getStates().size()){
                    for(j = 0; j < beforePart.get(i).getStates().size(); j++){
                        if(!beforePart.get(i).getStates().get(j).equals(afterPart.get(i).getStates().get(j)))
                            return false;
                    }
                } else return false;
                
                if(beforePart.get(i).getTransTable().size() == afterPart.get(i).getTransTable().size()){
                    for(j = 0; j < beforePart.get(i).getTransTable().size(); j++){
                        if(!beforePart.get(i).getTransTable().get(j).getFromState().equals(afterPart.get(i).getTransTable().get(j).getFromState()))
                            return false;
                        
                        if(beforePart.get(i).getTransTable().get(j).getOutputs().size() == afterPart.get(i).getTransTable().get(j).getOutputs().size()){
                            for(k = 0; k < beforePart.get(i).getTransTable().get(j).getOutputs().size(); k++){
                                if(!beforePart.get(i).getTransTable().get(j).getOutputs().get(k).equals(afterPart.get(i).getTransTable().get(j).getOutputs().get(k)))
                                    return false;
                            }
                        }  else return false;
                        
                        if(beforePart.get(i).getTransTable().get(j).getToState().size() == afterPart.get(i).getTransTable().get(j).getToState().size()){
                            for(k = 0; k < beforePart.get(i).getTransTable().get(j).getToState().size(); k++){
                                if(!beforePart.get(i).getTransTable().get(j).getToState().get(k).equals(afterPart.get(i).getTransTable().get(j).getToState().get(k)))
                                    return false;
                            }
                        }  else return false;
                    }
                } else return false;
            }
        } else return false;
        
        return true;
    }
    
    public static ArrayList<Partition> initialize(Machine machine){
        ArrayList<String> states = machine.getStates();
        ArrayList<Transition> transTable = machine.getTransTable();
        ArrayList<Partition> partitions = new ArrayList<Partition>();
        Partition temp;
        
        int blobLabel = 0;
        
        for(int i = 0; i < states.size(); i++){
            for(int j = 0; j < transTable.size(); j++){
                if(states.get(i).equals(transTable.get(j).getFromState())){
                    if(!checkIfExists(transTable.get(j).getOutputs(), partitions)){
                        temp = new Partition(transTable.get(i).getOutputs(), String.valueOf(blobLabel));
                        temp.addState(states.get(i));
                        partitions.add(temp);
                        blobLabel++;
                    } else {
                        for(int k = 0; k < partitions.size(); k++){
                            if(partitions.get(k).getOutputs().equals(transTable.get(j).getOutputs())){
                                partitions.get(k).addState(states.get(i));
                            }      
                        }
                    }
                }
            }
        }
        
        setTransitions(partitions, machine);
        return partitions;
    }
    
    public static void setTransitions(ArrayList<Partition> partitions, Machine machine){
        ArrayList<String> states = machine.getStates();
        ArrayList<Transition> transTable = machine.getTransTable();
        Transition temp;
                 
        for(int i = 0; i < states.size(); i++){
            for(int j = 0; j < partitions.size(); j++){
                for(int k = 0; k < partitions.get(j).getStates().size(); k++){
                    if(states.get(i).equals(partitions.get(j).getStates().get(k))){
                        temp = new Transition(states.get(i));
                        temp.setToState(transTable.get(i).getToState());
                        
                        for(int l = 0; l < temp.getToState().size(); l++){
                            for(int m = 0; m < partitions.size(); m++){
                                for(int n = 0; n < partitions.get(m).getStates().size(); n++){
                                    if(temp.getToState().get(l).equals(partitions.get(m).getStates().get(n)))
                                        temp.addOutput(String.valueOf(m));
                                }
                            }
                        }                     
                        partitions.get(j).addTransition(temp);
                    }
                }     
            }
        }
    }
    
    public static boolean checkIfExists(ArrayList<String> outputs, ArrayList<Partition> partitions){
        int check = 0;
        
        for(int i = 0; i < partitions.size(); i++){
            if(outputs.equals(partitions.get(i).getOutputs()))
                check++;     
        }
        
        return check > 0;
    }
    
    public static Machine readFile(String filename){
        ArrayList<String> states = new ArrayList<String>();
        ArrayList<String> inputs = new ArrayList<String>();
        ArrayList<String> outputs = new ArrayList<String>();
        String name = null;
        ArrayList<Transition> transTable = new ArrayList<Transition>();
        
        Path path = Paths.get(filename);
        Charset cs = StandardCharsets.ISO_8859_1;
        String temp;
        int rowAt = 1;
		
        try(BufferedReader reader = Files.newBufferedReader(path, cs)){
            while((temp = reader.readLine()) != null){
                String[] readLine = temp.split(",");
                
                if(rowAt == 1){
                    name = readLine[0];
                } else if(rowAt == 2){
                    for(int i = 0; i < readLine.length; i++)
                        states.add(readLine[i]);
                } else if(rowAt == 3){
                    for(int i = 0; i < readLine.length; i++)
                        inputs.add(readLine[i]);
                } else if(rowAt == 4){
                    for(int i = 0; i < readLine.length; i++)
                        outputs.add(readLine[i]);
                } else if(rowAt > 4){
                    Transition trans = new Transition(readLine[0]);
                    
                    for(int i = 1; i < readLine.length; i++){
                        String[] response = readLine[i].split(":");
                        trans.addResponseState(response[0], response[1]);
                    }
                    
                    transTable.add(trans);
                }
                    
                rowAt++;
            }			
      	} catch(IOException x){
            System.err.println(x);
      	}
        
        return new Machine(name, states, inputs, outputs, transTable);
    }
    
    public static void displayMachine(Machine machine){
        int i, j;
        
        System.out.println("States:");
        for(i = 0; i < machine.getStates().size(); i++){
            if(i == machine.getStates().size() - 1)
                System.out.println(machine.getStates().get(i));
            else
                System.out.print(machine.getStates().get(i) + ", ");
        }
            
        System.out.println("Inputs:");
        for(i = 0; i < machine.getInputs().size(); i++){
            if(i == machine.getInputs().size() - 1)
                System.out.println(machine.getInputs().get(i));
            else
                System.out.print(machine.getInputs().get(i) + ", ");
        }
        
        System.out.println("Outputs:");
        for(i = 0; i < machine.getOutputs().size(); i++){
            if(i == machine.getOutputs().size() - 1)
                System.out.println(machine.getOutputs().get(i));
            else
                System.out.print(machine.getOutputs().get(i) + ", ");
        }
        
        System.out.println("Transition Table:");
        for(i = 0; i < machine.getTransTable().size(); i++){
            System.out.print(machine.getTransTable().get(i).getFromState() + ", ");
            
            for(j = 0; j < machine.getTransTable().get(i).getToState().size(); j++){
                if(j == machine.getTransTable().get(i).getToState().size() - 1)
                    System.out.println(machine.getTransTable().get(i).getToState().get(j) + ":" +
                                   machine.getTransTable().get(i).getOutputs().get(j));
                else 
                    System.out.print(machine.getTransTable().get(i).getToState().get(j) + ":" +
                                   machine.getTransTable().get(i).getOutputs().get(j) + ", ");
            }
        }
    }
    
    public static void displayPartitions(ArrayList<Partition> partitions){
        int i, j;
        
        if(partitions.isEmpty()){
               System.out.println("No Partitions"); 
        } else{
            for(i = 0; i < partitions.size(); i++){
                System.out.println("Blob #" + partitions.get(i).getLabel());

                System.out.println("Outputs:");
                for(j = 0; j < partitions.get(i).getOutputs().size(); j++){
                    if(j == partitions.get(i).getOutputs().size() - 1)
                        System.out.println(partitions.get(i).getOutputs().get(j));
                    else
                        System.out.print(partitions.get(i).getOutputs().get(j) + ", ");
                }

                System.out.println("States:");
                for(j = 0; j < partitions.get(i).getStates().size(); j++){
                    if(j == partitions.get(i).getStates().size() - 1)
                        System.out.println(partitions.get(i).getStates().get(j));
                    else
                        System.out.print(partitions.get(i).getStates().get(j) + ", ");
                }
            }
        }
    }
    
    public static void displayTransitions(Machine machine1, Machine machine2, ArrayList<Partition> partitions){
        int i, j;
        
        if(partitions.isEmpty()){
            System.out.println("No Transitions");
        } else{
            System.out.println("Transition Table:");
            for(i = 0; i < partitions.size(); i++){           
                
                System.out.println("Blob #" + i);
                for(j = 0; j < partitions.get(i).getTransTable().size(); j++){
                    
                    if(machine1.getStates().contains(partitions.get(i).getTransTable().get(j).getFromState()))
                        System.out.print("From " + machine1.getName() + ":");
                    else if(machine2.getStates().contains(partitions.get(i).getTransTable().get(j).getFromState()))
                        System.out.print("From " + machine2.getName() + ":");
                    
                    System.out.print(partitions.get(i).getTransTable().get(j).getFromState() + ", ");
                    for(int k = 0; k < partitions.get(i).getTransTable().get(j).getToState().size(); k++){
                        if(k == partitions.get(i).getTransTable().get(j).getToState().size() - 1)
                            System.out.println(partitions.get(i).getTransTable().get(j).getToState().get(k) + ":" +
                                       partitions.get(i).getTransTable().get(j).getOutputs().get(k));
                        else 
                            System.out.print(partitions.get(i).getTransTable().get(j).getToState().get(k) + ":" +
                                       partitions.get(i).getTransTable().get(j).getOutputs().get(k) + ", ");
                    }              
                }
            }
        }
    }
}
