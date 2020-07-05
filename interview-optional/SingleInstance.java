package com.strands.spf;

/**
 * <p>
 * Single instance in a multi threading environment
 * </p>
 *
 * @author strands
 *
 */
public class SingleInstance {

  private static SingleInstance singleInstance;
  // Test attribute to verify singleton works
  private String s;

  private SingleInstance() {}

  public synchronized static SingleInstance getInstance() {
    if (singleInstance == null) {
      singleInstance = new SingleInstance();
    }
    return singleInstance;
  }

  public String getS() {
    return s;
  }

  public void setS(String s) {
    this.s = s;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {

    // Testing Singleton works by modifying test string value in '2 different instances'
    SingleInstance s1 = SingleInstance.getInstance();
    s1.setS("Testing 1");
    SingleInstance s2 = SingleInstance.getInstance();
    s2.setS("Testing 2");

    System.out.println(s1.getS());
    System.out.println(s2.getS());


  }

}
