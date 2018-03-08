package code.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import javax.swing.JButton;

import code.ui.UI;

public class Board {
	private ArrayList<ArrayList<String>> _board;
	private ArrayList<String> _colorFileNames;
	private Random _rand;
	private int _flag1 = 0;
	private int _flag2 = 1;
	private int _score = 0;
	private static int MAX_COLORS = 6; // max possible is 6
	
	private String _color1;
	private String _color2;
	private UI _ui;
	
	public void ui(UI ui){
		_ui = ui;
	}

	public Board(int rows, int cols) {
		_board = new ArrayList<ArrayList<String>>();
		_rand = new Random();
		_colorFileNames = new ArrayList<String>();
		for (int i=0; i<MAX_COLORS; i=i+1) {
			_colorFileNames.add("Images/Tile-"+i+".png");
		}
		listColor();
		refresh();
//		moveHint();
	}

	public int rows() { return _board.size(); }
	public int cols() { return _board.get(0).size(); }

	public String get(Point p) {
		return _board.get(p.x).get(p.y);
	}

	private String set(Point p, String s) {
		return _board.get(p.x).set(p.y, s);
	}

	public void exchange(Point p, Point q) {
		String temp = get(p);
		set(p, get(q));
		set(q, temp);
		if (match().size() > 0) {
			matchResponse(p);
			matchResponse(q);
			noMatchedAfterDrop(p,q);//to avoid new dropped icon cause a match			
		}
		else {
			String temp1 = get(p);
			set(p, get(q));
			set(q, temp1);
		}
    	_flag2 =1;
	    	noMatchLegalMove();
			if (_flag2 == 1){//if has no match
				System.out.println(_score);
				System.exit(0);
			}
	}
	
	private HashSet<Point> match() {
		return match(3);
	}

	private HashSet<Point> match(int runLength) {
		HashSet<Point> matches = verticalMatch(runLength);
		matches.addAll(horizontalMatch(runLength));
		return matches;
	}

	private HashSet<Point> horizontalMatch(int runLength) {
		HashSet<Point> matches = new HashSet<Point>();
		int minCol = 0;
		int maxCol = cols() - runLength;//maxCol = 2
		for (int r = 0; r < rows(); r = r + 1) {
			for (int c = minCol; c <= maxCol; c = c + 1) {  // The cols we can START checking in
				HashSet<String> values = new HashSet<String>();
				HashSet<Point> points = new HashSet<Point>();
				for (int offset = 0; offset < runLength; offset = offset + 1) {
					Point p = new Point(r,c+offset);
					points.add(p);
					String s = get(p);
					values.add(s);
				}
				if (values.size() == 1) { matches.addAll(points); }
			}
		}
		return matches;
	}

	private HashSet<Point> verticalMatch(int runLength) {
		HashSet<Point> matches = new HashSet<Point>();
		int minRow = 0;
		int maxRow = rows() - runLength;
		for (int c = 0; c < cols(); c = c + 1) {
			for (int r = minRow; r <= maxRow; r = r + 1) {  // The rows we can START checking in
				HashSet<String> values = new HashSet<String>();
				HashSet<Point> points = new HashSet<Point>();
				for (int offset = 0; offset < runLength; offset = offset + 1) {
					Point p = new Point(r+offset,c);
					points.add(p);
					String s = get(p);
					values.add(s);
				}
				if (values.size() == 1) { matches.addAll(points); }
			}
		}
		return matches;
	}
	
	private void listColor(){
		for (int r=0; r<5; r=r+1) {
		ArrayList<String> row = new ArrayList<String>();
			for (int c=0; c<5; c=c+1) {
				row.add(_colorFileNames.get(_rand.nextInt(_colorFileNames.size())));
			}
			_board.add(row);
		}		
	}
	
	private void refresh(){
		boolean stop = false;
	    while(stop != true){
	    	noMatchLegalMove();
			if (_flag1 ==0 & _flag2 == 0){//if no match and has legal move
				stop = true;
			}			
			else{//if has match or has no legal move
				_board.clear();
				listColor();
				_flag1=0;
				_flag2=1;
			}
		}
	}
	
	private void noMatchLegalMove(){
		for (int r=0;r<5;r++){
			for(int c=0;c<5;c++){
				String icon = _board.get(r).get(c);				
				HashSet<String> hashSet2= new HashSet<String>();
				HashSet<String> hashSet3= new HashSet<String>();
				if (r<3){//vertical |
					ArrayList<String> fourPointLine = new ArrayList<String>();
					String firstOfFour = null;
					if (r<=1){
						firstOfFour = (_board.get(r).get(c));
						fourPointLine.add(firstOfFour);
						for(int r1=r+1;r1<(r+4);r1++){
							if(fourPointLine.get(0).equals(_board.get(r1).get(c))){//if the every single three following points equals the first point
								fourPointLine.add(_board.get(r1).get(c));//then add that point to ArrayList,so in which all the String are the same
							}
						}
						changeFlag1(hashSet2,hashSet3,fourPointLine);						
					}
					if(c<4){
						_color1= _board.get(r).get(c);
						_color2= _board.get(r).get(c+1);
						hashSet2.add(_board.get(r+1).get(c));
						hashSet2.add(_board.get(r+1).get(c+1));
						hashSet3.add(_board.get(r+2).get(c));
						hashSet3.add(_board.get(r+2).get(c+1));
						changeFlag1(hashSet2,hashSet3,fourPointLine);
					}
					String icon1 = _board.get(r+1).get(c);
					String icon2 = _board.get(r+2).get(c);
					changeFlag(icon,icon1,icon2);
				}
				hashSet2= new HashSet<String>();
				hashSet3= new HashSet<String>();
				if (c<3){//horizontal ---
					ArrayList<String> fourPointLine = new ArrayList<String>();
					if (c<=1){
						String firstOfFour = (_board.get(r).get(c));
						fourPointLine.add(firstOfFour);
						for(int c1=c+1;c1<(c+4);c1++){
							if(fourPointLine.get(0).equals(_board.get(r).get(c1))){//if the every single three following points equals the first point
								fourPointLine.add(_board.get(r).get(c1));//then add that point to ArrayList,so in which all the String are the same
							}
						}
						changeFlag1(hashSet2,hashSet3,fourPointLine);						
					}
					if(r<4){
						_color1= _board.get(r).get(c);
						_color2= _board.get(r+1).get(c);
						hashSet2.add(_board.get(r).get(c+1));
						hashSet2.add(_board.get(r+1).get(c+1));
						hashSet3.add(_board.get(r).get(c+2));
						hashSet3.add(_board.get(r+1).get(c+2));
						changeFlag1(hashSet2,hashSet3,fourPointLine);
					}					
					String icon1 = _board.get(r).get(c+1);
					String icon2 = _board.get(r).get(c+2);
					changeFlag(icon,icon1,icon2);			
				}
			}
		}
	}
	
	private void changeFlag(String icon, String icon1,String icon2){
		if (icon.equals(icon1) && icon1.equals(icon2)){
			_flag1 = 1;//has match
		}		
	}
	
	private void changeFlag1(HashSet<String> hashSet2,HashSet<String> hashSet3, ArrayList<String> fourPointLine){
		if(((hashSet2.contains(_color1) && hashSet3.contains(_color1)) || (hashSet2.contains(_color2) && hashSet3.contains(_color2)))||(fourPointLine.size()==3)){
			_flag2 = 0;//has legal move
		}
	}
	
	public void matchResponse(Point o){
//		Point p = moveHint();
//		_ui.removeHighlight(p);
		ArrayList<Point> array = new ArrayList<Point>();
		for(Point s:maximalMatchedRegion(o)){
			 _board.get(s.x).set(s.y,"null");
			 array.add(s);
		}
		int x = array.size();
			if(x!=0){
				_score = _score + 3 +(x-3)*(x-3);
			}
		for(Point i:maximalMatchedRegion(o)){
			if (_board.get(i.x).get(i.y)=="null"){
				boolean stop= false;
				while(stop == false){
					dropDown(i);
					if (_board.get(i.x).get(i.y)!="null"){
						stop = true;
					}
				}
			}
		}
//		moveHint();
	}
	
	public void noMatchedAfterDrop(Point a,Point b){
		boolean stop = false;
		while(stop!=true){
			for(int r=0;r<5;r++){
				for(int c=0;c<5;c++){
					ArrayList<Point> array = new ArrayList<Point>();
					Point p = new Point(r,c);
					for(Point s:maximalMatchedRegion(p)){
						 array.add(s);
					}
					if(array.size()>=3){//has other matched points
						r=4;
						c=4;
						matchResponse(p);
					}
					else if((r==4 & c==4)&& array.size()<3){
						stop=true;
					}
				}
			}

		}
	}
	
	private HashSet<Point> maximalMatchedRegion(Point start) {//add all the up down right left point matched the point o including o
		HashSet<Point> matchedRegion = new HashSet<Point>();
		HashSet<Point> finalMatchedRegion = new HashSet<Point>();
		HashSet<Point> candidatesForInclusion = new HashSet<Point>();
		HashSet<Point> adjacentMatches = new HashSet<Point>();
		candidatesForInclusion.add(start);
			while (! candidatesForInclusion.isEmpty()) {
				for (Point p : candidatesForInclusion) {
					for (Point q : upDownAndMatching(p)) {
						if (!matchedRegion.contains(q) && !candidatesForInclusion.contains(q)) {
							adjacentMatches.add(q);
						}
					}
				}
				matchedRegion.addAll(candidatesForInclusion);
				HashSet<Point> tmp = candidatesForInclusion;
				candidatesForInclusion = adjacentMatches;
				adjacentMatches = tmp;
				adjacentMatches.clear();
			}
			ArrayList<Point> ap = new ArrayList<Point>();
			for(Point o:matchedRegion){
				ap.add(o);
			}
			if (ap.size()>=3){
				finalMatchedRegion.addAll(matchedRegion);
			}
			matchedRegion.clear();
			ap.clear();
			
			candidatesForInclusion.add(start);
			while (! candidatesForInclusion.isEmpty()) {
				for (Point p : candidatesForInclusion) {
					for (Point q : leftRightAndMatching(p)) {
						if (!matchedRegion.contains(q) && !candidatesForInclusion.contains(q)) {
							adjacentMatches.add(q);
						}
					}
				}
				matchedRegion.addAll(candidatesForInclusion);
				HashSet<Point> tmp = candidatesForInclusion;
				candidatesForInclusion = adjacentMatches;
				adjacentMatches = tmp;
				adjacentMatches.clear();
			}
			for(Point o:matchedRegion){
				ap.add(o);
			}
			if (ap.size()>=3){
				finalMatchedRegion.addAll(matchedRegion);
			}
			return finalMatchedRegion;
	}
	public HashSet<Point> leftRightAndMatching(Point p){//get matched point up and down
		HashSet<Point> match = new HashSet<Point>();
		if(p!=null){
			if (p.y-1 >=0){
				if(_board.get(p.x).get(p.y).equals(_board.get(p.x).get(p.y-1))){
					Point o = new Point(p.x,p.y-1);
					match.add(o);					
				}
			}
			if (p.y+1 < _board.size()){
				if(_board.get(p.x).get(p.y).equals(_board.get(p.x).get(p.y+1))){
					Point o = new Point(p.x,p.y+1);
					match.add(o);		
			    }
			}
		}		
		return match;
	}	
	
	public HashSet<Point> upDownAndMatching(Point p){//get matched point on left and right
		HashSet<Point> match = new HashSet<Point>();
		if(p!=null){
			if (p.x-1 >=0){
				if(_board.get(p.x).get(p.y).equals(_board.get(p.x-1).get(p.y))){
					Point o = new Point(p.x-1,p.y);
					match.add(o);					
			    }
			}
			if (p.x+1 < _board.get(0).size()){
				if(_board.get(p.x).get(p.y).equals(_board.get(p.x+1).get(p.y))){
					Point o = new Point(p.x+1,p.y);
					match.add(o);					
				}
			}
		}		
		return match;
	}	
	
	public void dropDown(Point s){
		for(int x = s.x;x>0;x--){
			_board.get(x).set(s.y, _board.get(x-1).get(s.y));
		}
		_board.get(0).set(s.y, _colorFileNames.get(_rand.nextInt(_colorFileNames.size())));
	}
	
	public Point viewBoard(){
		return moveHint();
	}
	public Point moveHint(){
		Point flag = new Point(5,5);
		for (int x=0;x<5;x++){
			for(int y=0;y<5;y++){
				if(y<4){//possible move horizontally
					String temp = (_board.get(x).get(y));
					_board.get(x).set(y, _board.get(x).get(y+1));
					_board.get(x).set(y+1, temp);
					noMatchLegalMove();
					_board.get(x).set(y, temp);
					_board.get(x).set(y+1, _board.get(x).get(y+1));
					if(_flag1==1){
						
						flag.setLocation(x,y);//set the background of button to blue;
					    _ui.moveHintHighlight(flag);
						x=5;
						y=5;
					}
					_flag1=0;

				}
				if(x<4){//possible move vertically					
					String temp = (_board.get(x).get(y));
					_board.get(x).set(y, _board.get(x+1).get(y));
					_board.get(x+1).set(y, temp);
					_board.get(x).set(y, temp);
					_board.get(x+1).set(y, _board.get(x+1).get(y));
					if(_flag1==1){
						flag.setLocation(x, y);
					    _ui.moveHintHighlight(flag);
						x=5;
						y=5;
					}
					_flag1=0;
				}
			}
		}
		return flag;
	}
}
