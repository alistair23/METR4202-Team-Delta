package functions;

public class TEST {
	   static {
	      System.loadLibrary("hello"); // hello.dll (Windows) or libhello.so (Unixes)
	   }
	   // A native method that receives nothing and returns void
	   private native void sayHello();
	 
	   public static void main(String[] args) {
	      new TEST().sayHello();  // invoke the native method
	   }
	}