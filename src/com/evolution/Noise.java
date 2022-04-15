package com.evolution;
import java.util.Random;
import java.util.Date;

public class Noise {

	private long seed_;   //seed for random number generator
	private Random rand_; //pseudorandom number generator
	private Date date_;   //gets time

	//  Constructor with seed
	public Noise(long seed) {
		seed_ = seed;
		rand_ = new Random(seed_);
	}

	//  Constructor without seed
	public Noise() {
		date_ = new Date();
		seed_ = date_.getTime();
		rand_ = new Random(seed_);
	}

	//  sets seed
	public void setSeed(int seed) {
		seed_ = seed;
		rand_.setSeed(seed_);
	}

	//  generates perlin noise
	public int[] generatePerlinNoise(int length, double roughness) {
		int[] noise = new int[length];
		double x;

		for (int i=0;i<length;i++) {
			x = i*roughness;
			noise[i] = (int)(perlinNoise_(x)*0xff);
		}
		return noise;
	}

	//  generates perlin noise at a
	//  specific coordinate
	private double perlinNoise_(double x) {

		double r = x%(1.0); //position of x relative to
		                    //the surrounding unit line
		int x0 = (int)x;
		int x1 = (int)x+1;

		double x0v, x1v; //random unit vectors from x0 and x1

		rand_.setSeed(seed_+x0); rand_.nextDouble();
		x0v = (rand_.nextDouble()*2)-1;

		rand_.setSeed(seed_+x1); rand_.nextDouble();
		x1v = (rand_.nextDouble()*2)-1;

		double x0d = 1-r; //distance vector from x0 to x
		double x1d = r;   //distance vector from x1 to x

		double x0i = x0v*x0d; //x0 influence
		double x1i = x1v*x1d; //x0 influence

		//System.out.printf("x0v: %f, x1v: %f, x0d: %f, x1d: %f, x0i: %f, x1i: %f, r: %f\n",x0v,x1v,x0d,x1d,x0i,x1i,fade_(r));

		r = fade_(r);
		return (lerp_(x0i,x1i,r)+1)/2; //average influence
	}

	private double fade_(double x) {return x*x*x*(x*(x*6-15)+10);}
	private double lerp_(double a, double b, double x) {
		return (b-a)*x+a;
	}
}
