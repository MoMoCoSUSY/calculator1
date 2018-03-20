public class ResultTransformer {
	private ResultTransformer() {
		
	}
	
	public static String toScientific(String in) {
		return String.format("%e", Double.parseDouble(in));
	}
	
	public static String radixTransform(String in,int radix) {
		int n=(int)Double.parseDouble(in);
		StringBuffer result=new StringBuffer("");
		getNum(result,n,radix);
		return result.toString().equals("")? "0":result.toString();
	}
	
	private static void getNum(StringBuffer result,int n,int radix) {
		int m,r;
		m=n/radix;
		r=n%radix;
		if(m==0){
			if(r!=0)
				result.append(change(r));
			return;
		}
		getNum(result,m,radix);
		result.append(
				change(r));
	}
	
	private static String change(int n) {
		if(n<=9) return n+"";
		return (char)('A'+n-10)+"";
	}
}
