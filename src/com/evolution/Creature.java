package com.evolution;
import java.awt.Color;
import java.lang.Math;
import java.util.Random;

public class Creature {
	private NeuralNet brain; //neural network generated based on dna
	private Random rand;     //random number generator
	public int[] dna;        //dna used to generate brain
	public Color color;      //color of creature; based on dna
	private int hnum;        //number of hidden neurons

	public int x,y;                 //location
	public int nearbyCreatures = 0; //number of nearby creatures
	public int age = 0;             //age of creature

	public boolean moveNorth = false; //if should move north
	public boolean moveEast  = false; //               east
	public boolean moveSouth = false; //               south
	public boolean moveWest  = false; //               west
	
	public Creature(int[] dna_in, int x_in, int y_in, int hnum_in) {

		dna = dna_in;
		x = x_in;
		y = y_in;
		hnum = hnum_in;
		brain = new NeuralNet(dna,hnum);
		rand = new Random();

		//  get color by magic
		Noise noise = new Noise();
		int[] perlin_noise = noise.generatePerlinNoise(dna.length*32+41,0.1);
		int sum = 0;
		for (int gene : dna) {
			for (int i=0;i<32;i++) {
				sum += (gene>>i)&0x01;
			}
		}
		int r = perlin_noise[sum];
		int g = perlin_noise[sum+20];
		int b = perlin_noise[sum+40];
		color = new Color(r,g,b);
	}

	public void think() {
		double[] senses = {x, y, nearbyCreatures, age, 1}; //x, y, nearby creatures, age, constant ON
		double[] actions = brain.think(senses);
		if (actions[0] >= 0) {moveNorth = true;} else {moveNorth = false;}
		if (actions[1] >= 0) {moveEast  = true;} else {moveEast  = false;}
		if (actions[2] >= 0) {moveSouth = true;} else {moveSouth = false;}
		if (actions[3] >= 0) {moveWest  = true;} else {moveWest  = false;}
	}

	public Creature makeBaby(int new_x, int new_y, double mutation_rate) {

		//  mutate dna then make
		//  new creature
		int[] baby_dna = dna.clone();
		int percent = (int)(mutation_rate*100);
		int gene;

		for (int i=0;i<dna.length;i++) {
			for (int bit=0;bit<32;bit++) {
				if (rand.nextInt(100) < percent) baby_dna[i] ^= (0x1<<bit);
			}
		}
		return new Creature(baby_dna,new_x,new_y,hnum);
	}
}
