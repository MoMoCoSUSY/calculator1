import java.util.*;

import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;

public class CalculatorLogic {
	private ArrayList<Token> tokenStream;
	private Map<String,Double> IdMap;
	private TokenProcessor tokenProcessor=new TokenProcessor();
	private ComputeExecutor computeExe=new ComputeExecutor();
	private Scanner input=new Scanner(System.in);
	private String[] functionLib={
			"cos","sin","tan","log","pow","sqrt",
			"log","ln","exp","PI","E","rand","rInt"
			};
/*
	public static void main(String[] args) {
		CalculatorLogic test=new CalculatorLogic();
		Scanner input=new Scanner(System.in);
		while(true) {
			test.scan(input.nextLine());
		}
	}
*/
	
	public void scan(String expression) throws Exception{
		tokenStream=new ArrayList<>();
		IdMap=new LinkedHashMap<>();
		tokenProcessor.getTokenStream(expression);
	}
	
	public double compute() throws Exception{
		return computeExe.exeComputing(tokenStream,IdMap);
	}
	public double compute(ArrayList<Token> tokenStream,Map<String,Double> IdMap) throws Exception{
		return computeExe.exeComputing(tokenStream,IdMap);
	}
	
	private class TokenProcessor{
		public void createToken(String word) throws Exception{
			if(fitNum(word))
				tokenStream.add(new Token(3,word));
			else if(fitFounction(word))
				tokenStream.add(new Token(4,word));
			else if(fitId(word)) {
				tokenStream.add(new Token(5,word));
				IdMap.put(word, null);
			}
			else throw new Exception("Illegal chatacter or number! Try again");
		}
		
		public void getTokenStream(String input) throws Exception {
			StringBuffer tmp=new StringBuffer();
			for(int i=0;i<input.length();i++) {
				char ch=input.charAt(i);
				if(ch==' ' || fitSep(ch+"") || fitOP(ch+"")) {
					if(tmp.length()!=0) {
						createToken(tmp.toString());
						tmp=new StringBuffer("");
					}
					if(fitSep(ch+""))
						tokenStream.add(new Token(1,ch+""));
					else if(fitOP(ch+""))
						tokenStream.add(new Token(2,ch+""));
				}
				else 
					tmp.append(ch);
				}
			if(tmp.length()!=0)
				createToken(tmp.toString());
			if(tokenStream.size()==0)
				throw new Exception("Empty Input!");
		}
		
		public boolean fitSep(String word){
			char ch=word.charAt(0);
			return word.length()==1 && (ch=='(' || ch==')'|| ch==',' || ch=='%');
		}
		
		public boolean fitOP(String word) {
			char ch=word.charAt(0);
			return word.length()==1 && (ch=='+' ||ch=='-'|| ch=='*'||ch=='/');
		}
		
		public boolean fitNum(String word) {
			try {
				if(word.contains("."))
					Double.parseDouble(word);
				else Integer.parseInt(word);
				return true;
			}catch(Exception e) {
				return false;
			}
		}
		
		public boolean fitFounction(String word) {
			for(String f:functionLib)
				if(word.equals(f))
					return true;
			return false;
			
		}
		public boolean fitId(String word) {
			int state=0;
			char ch='$';
			for(int i=0;i<word.length();i++) {
				ch=word.charAt(i);
				if(state==0) {
					if(ch=='$'||ch=='_'||ch<='z' && ch>='a' || ch<='Z' &&ch>='A')
						state=1;
					else return false;
				}
				else if(state==1) {
		
					if(ch<='z' && ch>='a' || ch<='Z' && ch>='A' || ch<='9' &&ch>='0')
						continue;
					else return false;
				}
			}
			return word.length()>1 || ch!='_' && ch!='$';
		}
	}
	
	private class ComputeExecutor{
		private ArrayList<Token> tokenStream;
		private Map<String,Double> IdMap;
		private int currIndex=0;
		private Token currToken;
		private String con;
		private int cat;
		
		public double exeComputing(ArrayList<Token> tokenStream,Map<String,Double> IdMap) throws Exception{
			this.tokenStream=tokenStream;
			this.IdMap=IdMap;
			currIndex=0;
			nextToken();
			try {
				return E();
			}catch(Exception e) {
				throw new Exception("Syntax Exception!Try agagin");
			}
		}
		
		private void nextToken(){
			// Warning i out of Bound !!
			if(currIndex<tokenStream.size()) {
				currToken=tokenStream.get(currIndex++);
				con=currToken.content;
				cat=currToken.cat;
			}
			else {
				currToken=null;
				con="$";
				cat=-1;
			}
		}
		
		private void match(String str) throws Exception{
			if(! con.equals(str))
				throw new Exception();
			else 
				nextToken();
		}
		
		private double E() throws Exception{
			double n;
			if(con.equals("+") || con.equals("-")) {
				char op=con.charAt(0);
				nextToken();
				n=  op=='+'? +T():-T() ;
				n=E1(n);
			}
			else if(cat==3 || cat==4 || cat==5 || con.equals("(")){
				n=T();
				n=E1(n);
			}			
			else throw new Exception();
			return n;
		}
		
		private double E1(double in) throws Exception{
			double n;
			if(con.equals("+") || con.equals("-")) {
				char op=con.charAt(0);
				nextToken();
				n= op=='+' ? in+T():in-T();
				n=E1(n);
			}
			else if(con.equals(")" )|| con.equals("$") || con.equals(","))
				n=in;
			else throw new Exception();
			return n;
		}
		
		private double T() throws Exception{
			double n;
			if(cat==3||cat==4||cat==5 || con.equals("(")) {
				n=F();
				n=T1(n);
			}
			else throw new Exception();
			return n;
		}
		
		private double T1(double in) throws Exception{
			double n;
			if(con.equals("/") || con.equals("*")) {
				char op=con.charAt(0);
				nextToken();
				n= op=='*' ? in*F():in/F();
				n=T1(n);
			}
			else if(con.equals("+") || con.equals("-") || con.equals(")")
					|| con.equals("$") || con.equals(",")) {
				n=in;
			}
			else throw new Exception();	
			return n;
		}
		
		private double F() throws Exception{
			double n;
			if(cat==3) {
				n=Double.parseDouble(con);
				nextToken();
				if(con.equals("%")) {
					n=n/100;
					nextToken();
				}
			}
			else if(cat==5) {
				n=IdMap.get(con);
				nextToken();
				if(con.equals("%")) {
					n=n/100;
					nextToken();
				}
			}
			else if(con.equals("(")) {
				nextToken();
				n=E();
				match(")");
				if(con.equals("%")) {
					n=n/100;
					nextToken();
				}
			}
			else if(cat==4){
				n=Function();
				if(con.equals("%")) {
					n=n/100;
					nextToken();
				}
			}
			else throw new Exception();
			return n;
		}
		
		private double Function() throws Exception{
			double n=0,a=0,b=0;
			String func=con;
			if(con.equals("PI")||con.equals("E")||con.equals("rand"))
				nextToken();
			else if(con.equals("cos") || con.equals("sin")
				|| con.equals("tan") || con.equals("sqrt")
				|| con.equals("ln") || con.equals("exp")
				|| con.equals("rInt")) {
				nextToken();match("("); a=E(); match(")");
			}
			else {
				nextToken(); match("("); a=E(); 
				match(","); b=E(); match(")");
			}
			
			switch(func){
				case "cos": n=Math.cos(a);break;
				case "sin": n=Math.sin(a);break;
				case "tan": n=Math.tan(a);break;
				case "sqrt": n=Math.sqrt(a);break;
				case "ln": n=Math.log(a);break;
				case "log": n=Math.log(b)/Math.log(a); break;
				case "pow": n=Math.pow(a, b);break;
				case "exp": n=Math.exp(a);break;
				case "rand": n=Math.random();break;
				case "rInt" :n=(int)(a*Math.random());break;
				case "PI": n=Math.PI;break;
				case "E": n=Math.E;break;
			}
			return n;
		}
	}
	
	public Map<String,Double> getIdMap(){
		return IdMap;
	}
	public ArrayList<Token> getTokenStream() {
		return tokenStream;
	}
}

class Token{
	int cat;
	String content;
	
	/*
	 * cat (category) is token type:
	 * 	1 separator : '(' or ')';
	 * 	2 arithmetic operator : '+','-','*','/'; 
		 * 	3 number
	 * 	4 special function
	 * 	5 identifier 
	 * 
	 * content means specific input;
		 */ 
	
	public Token(int cat,String content) {
		this.cat=cat;
		this.content=content;
	}
}