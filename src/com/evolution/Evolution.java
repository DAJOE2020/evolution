package com.evolution;

import java.lang.Math;
import java.io.*;
import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.time.Duration;
import java.time.Instant;
import java.lang.Math;

class Evolution {
	public static void main(String[] args) {

		final Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize(); //screen size

		final int w = 1536;                      //width of window
		final int h = 864;                       //height of window
		final int ox = (screen_size.width-w)/2;  //x offset of window
		final int oy = (screen_size.height-h)/2; //y offset of window
		final String title = "Creatures";        //title of window

		final int cnum = 10000;      //number of creatures
		final int gnum = 16;         //number of genes
		final int hnum = 4;          //number of hidden neurons
		final double mChance = 0.01; //chance for a bit to flip

		final Color backgroundColor = new Color(0xffffffff);       //color of background
		final int fps = 1000;                                      //frames per second
		final int tpf = 20;                                        //ticks per frame
		final int genTime = 600;                                   //length of a generation
		Random rand = new Random();                                //random number generator
		ArrayList<Creature> survivors = new ArrayList<Creature>(); //arraylist of fit enough population
		int sIndex;                                                //index of the survivor chosen to populate
		Creature baby;                                             //baby of chosen populator

		Instant start;       //time at start of frame
		Instant end;         //time at end of frame
		Duration frame_time; //length of frame
		int wait_time;       //time to wait to fit fps
		long total = 0;      //total time
		int fcount = 0;      //number of frames

		//  send warning if scale isn't a factor
		//  of either window width or height
		final int scale = 3;
		if (w % scale != 0) {System.out.printf("\u001B[01;33mWARNING: \u001B[0mscale is not a factor of screen width\n");}
		if (h % scale != 0) {System.out.printf("\u001B[01;33mWARNING: \u001B[0mscale is not a factor of screen height\n");}

		//  make a window
		Screen screen = new Screen(w,h,ox,oy,title);
		screen.setIcon("icon.png");

		//  generates cnum creatures
		System.out.println("Generating creatures!");
		Creature[] creatures = new Creature[cnum];
		int[] dna = new int[gnum];
		int x, y;
		for (int i=0;i<cnum;i++) {
			for (int j=0;j<gnum;j++) {
				dna[j] = rand.nextInt();
			}
			x = rand.nextInt(w/scale);
			y = rand.nextInt(h/scale);
			creatures[i] = new Creature(dna,x,y,hnum);
		}
		System.out.println("Done!");

		// loops all the generations
		while (true) {

			// loop activites of each generation
			for (int time=0;time<genTime/tpf;time++) {

				//System.out.printf("%f%%\r",(time*100.0)/genTime);
				start = Instant.now();

				//ticks
				for (int t=0;t<tpf;t++) {
					// have each creature move
					// according to their brains
					for (Creature c : creatures) {
						c.nearbyCreatures = 0;
						c.age = time;
						c.think();

						if (c.moveNorth) {c.y--;}
						if (c.moveEast) {c.x++;}
						if (c.moveSouth) {c.y++;}
						if (c.moveWest) {c.x--;}
						c.x = Math.max(c.x,0);
						c.x = Math.min(c.x,w/scale);
						c.y = Math.max(c.y,0);
						c.y = Math.min(c.y,h/scale);
					}
				}

				//  draw screen
				screen.clearToColor(backgroundColor);
				for (Creature c : creatures) {
					screen.drawRect(c.x*scale,c.y*scale,scale,scale,c.color);
				}
				screen.flip();

				end = Instant.now();
				frame_time = Duration.between(start,end);
				total += frame_time.toMillis();
				wait_time = Math.max(0,(1000/fps)-(int)frame_time.toMillis());
				System.out.printf("Frames take an average of %dms      \r",total/(++fcount));

				try {Thread.sleep(wait_time);} catch (InterruptedException e) {}
			}

			//  grab all survivors and
			//  populate until full
			for (Creature c : creatures) {
				if ((w/scale)/3 < c.x && 
					c.x < (w/scale*2)/3 && 
					(h/scale)/3 < c.y && 
					c.y < (h/scale*2)/3) {
					c.x = rand.nextInt(w/scale);
					c.y = rand.nextInt(h/scale);
					survivors.add(c);
				}
			}
			System.out.printf("%f%% survived!               \n",(survivors.size()*100.0)/cnum);
			while (survivors.size() < cnum) {
				sIndex = rand.nextInt(survivors.size());
				baby = survivors.get(sIndex).makeBaby(
					rand.nextInt(w/scale),
					rand.nextInt(h/scale),
					mChance
				);
				survivors.add(baby);
			}

			//  overwrite previous creatures with
			//  surviving popluation
			for (int i=0;i<cnum;i++) creatures[i] = survivors.get(i);
			survivors.clear();
		}
	}
}
