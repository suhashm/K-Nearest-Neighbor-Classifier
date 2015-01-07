package com.thinkjs.io;

public class PQ implements Comparable<PQ>{
	
		int index;
		int cl;
		Double dist;
		
		PQ(int a , int b, double c){
			this.index = a;
			this.cl = b;
			this.dist = c;
		}
		
		public int getIndex(){
			return index;
		}
		
		public int getClassLabel(){
			return cl;
		}
		
		public double getDist(){
			return dist;
		}
		@Override
		public int compareTo(PQ other) {
			// TODO Auto-generated method stub
			return this.dist.compareTo(other.dist);
			//return 0;
		}
		
		public boolean equals(PQ other){
			return dist == other.dist;
		}
	
}
