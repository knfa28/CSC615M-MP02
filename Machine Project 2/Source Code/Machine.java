package main;

import java.util.ArrayList;

public class Machine {
    private String name;
    private ArrayList<String> states;
    private ArrayList<String> inputs;
    private ArrayList<String> outputs;
    private String start;
    private ArrayList<Transition> transTable;

    public Machine(String name) {
        this.name = name;
        this.states = new ArrayList<String>();
        this.transTable = new ArrayList<Transition>();
    }
    
    public Machine(String name, ArrayList<String> states, ArrayList<String> inputs, ArrayList<String> outputs, ArrayList<Transition> transTable) {
        this.name = name;
        this.states = states;
        this.inputs = inputs;
        this.outputs = outputs;
        this.start = states.get(0);
        this.transTable = transTable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public ArrayList<String> getStates() {
        return states;
    }

    public void setStates(ArrayList<String> states) {
        this.states = states;
    }
    
    public void addStates(ArrayList<String> states) {
        this.states.addAll(states);
    }

    public ArrayList<String> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<String> inputs) {
        this.inputs = inputs;
    }

    public ArrayList<String> getOutputs() {
        return outputs;
    }

    public void setOutputs(ArrayList<String> outputs) {
        this.outputs = outputs;
    }
    
    public ArrayList<Transition> getTransTable() {
        return transTable;
    }

    public void setTransTable(ArrayList<Transition> transTable) {
        this.transTable = transTable;
    }
    
    public void addTransTable(ArrayList<Transition> transTable) {
        this.transTable.addAll(transTable);
    }
}
