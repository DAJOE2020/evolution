package com.evolution;

import java.io.*;
import java.lang.Math;
import java.util.ArrayList;

public class NeuralNet {
	
	final int in_neurons = 5;  //x, y, nearby creatures, age, constant ON
	final int out_neurons = 4; //move north, east, south, west
	int hnum;                  //number of hidden neurons
	int nnum;                  //number of neurons
	double[] now_values;       //current values
	double[] next_values;      //next values
	double[][] weights;        //weights

	public NeuralNet(int[] dna, int hnum_in) {
		hnum = hnum_in;
		nnum = in_neurons + hnum + out_neurons;

		now_values = new double[in_neurons+hnum];
		next_values = new double[hnum+out_neurons];
		weights = new double[in_neurons+hnum][hnum+out_neurons];

		//  grab brain connections from DNA
		int n0, n1;
		double weight;
		for (int gene : dna) {

			//  from neuron index is bits 1 to 8 // first byte
			//  to neuron index is bits 9 to 16  // second byte
			//  weight is bits 17 to 32          // third & fourth byte

			n0 = ((gene>>24)&0xFF)%(in_neurons+hnum);
			n1 = ((gene>>16)&0xFF)%(out_neurons+hnum);
			weight = ((short)(gene&0xFFFF))/8192.0;
			weights[n0][n1] = weight;
		}
	}
	
	private double[] matrixMult(double[] m1, double[][] m2) {
		double[] out = new double[m2.length];
		double val;

		for (int i=0;i<m2[0].length;i++) {
			val = 0;
			for (int j=0;j<m1.length;j++) {
				val += m1[j]*m2[j][i];
			}
			out[i] = val;
		}
		return out;
	}

	public double[] think(double[] senses) {

		//  write senses to now_values
		for (int i=0;i<in_neurons;i++) {now_values[i] = senses[i];}

		//  set next_values to the multiplication
		//  of current neuron values and weights
		next_values = matrixMult(now_values,weights);
		
		//  grab actions from neuron outputs and
		//  overwrite hidden neuron values
		double[] actions = new double[out_neurons];
		for (int i=0;i<out_neurons;i++) {actions[i] = Math.tanh(next_values[hnum+i]);}
		for (int i=0;i<hnum;i++) {now_values[i+in_neurons] = next_values[i];}
		return actions;
	}
}
