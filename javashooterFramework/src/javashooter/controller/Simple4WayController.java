package javashooter.controller;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javashooter.controller.*;
import javashooter.gameobjects.*;
import javashooter.gameutils.*;
import javashooter.playground.*;

import java.util.*;
import java.awt.event.*;


/** Simple javashooter.controller for object movable in 4 directions.
  * Key strokes are not read directly but taken from level flags
  */
public class Simple4WayController extends ObjectController {
  double rad = 0;
  double speed = 1.0 ;

  GameObject ego = null ;
  protected int heading = 4 ; // index into pressedEvts
  /** These are the names of key flags as generated by a LevelController instance. Define here 
   *  what key events should be reacted to */
  String [] pressedEvts = {"right","left","up","down" } ;
  
  /** .. and what the directions are for each key */
  int [] dxs = {1,-1,0,0,0} ;
  int [] dys = {0,0,-1,1,0} ;



  public Simple4WayController() {
    super() ;
  }
  
  
  public Simple4WayController setSpeed(double s) {
    this.speed = s ;
    return this ;
  }

  public String[] allowedEvents() {
    return this.pressedEvts ;
  }


  public int[] getDXs() {
    return this.dxs ;
  }


  public int[] getDYs() {
    return this.dys ;
  }
  
  public int getStopCode() {
	  return 4 ;
  }


  /** Computes the state the player wants based on key presses. Assume keys are signaled by flags of 
   *  keyname_pressed an keyname_released. Logic:
   *   * if key is pressed that selects a heading that is different from the current heading --> return new heading. 
   *   * If keys is released that is same as current heading --> stop. 
   *   * Without key events, return current heading
 * @return: -1 (stop), 0(right), ...
 */ 
  public int  computeRequestedState() {
    String[] pe = this.allowedEvents() ;
    for (int i = 0; i < pe.length; i ++) {
      LevelFlag<Boolean> pr = new LevelFlag<Boolean>(pe[i]+"_pressed", false) ;
      LevelFlag<Boolean> rel = new LevelFlag<Boolean>(pe[i]+"_released", false) ;
      if ((this.heading != i) && (pr.get()))  {
        //System.out.println("key"+i) ;
        return i ;
      }
      if ((this.heading == i) && (rel.get())) {
        return getStopCode() ;
      }
        
    }
    return this.heading ;  
  }


  public double getMovementSpeed() {
    return this.speed ;
  }

  public void executeMovement(int reqState) {
    int[]_dxs = this.getDXs() ;
    int[] _dys = this.getDYs() ;
    double vx = _dxs[reqState] * this.getMovementSpeed();
    double vy = _dys[reqState] * this.getMovementSpeed();
    this.setVX(vx) ;
    this.setVY(vy) ;
    this.heading = reqState ;
    //System.out.println("v="+vx+"/"+vy) ;
  }
  
  
  public boolean movementPossible(int reqState) {
    return true; 
  }
  
  
  public void illegalMovement() {
    setVX(0) ;
    setVY(0) ;
    this.heading = getStopCode() ;
  }
  
  
  public void ignoreMovement() {} ;
  


  public void updateObject() {

    int requestedState = this.computeRequestedState() ;
    
    // if we can execute the desired heading, do so ...
    if (movementPossible(requestedState)) {
      this.executeMovement(requestedState) ;
    } else {
      // otherwise: if we demand to go in the current heading dir. but cannoot --> stop
      if (this.heading == requestedState) {
        this.illegalMovement() ;
      } else {
        // if we demand to go in an impossible direction that is not the current heading --> do nothing
        this.ignoreMovement() ;
      }      
    }

    // updateSpeed and position
    applySpeedVector();
    
  }


}
