import Foundation

/**
* Used handle the entering of data from the user
**/
public class GamePad {
    
    private static var keysAreDown : Array<Bool> = Array<Bool>(count: GamePadEnum.NumberOfButtons.rawValue, repeatedValue: false)
    
    /**
    *
    * @param selectedKey key to check
    *
    * @return  Flag that indicates if the key was pressed or not
    */
    public static func isKeyDown(selectedKey : GamePadEnum) -> Bool {
        let value = keysAreDown[selectedKey.rawValue];
        GamePad.setKey(selectedKey, clicked: false);
        return value;
    }
    
    /**
    * Called when the user presses on of the virtual buttons
    *
    * @param selectedKey the key that the user have clicked
    * @param clicked       Flag that indicates if is to indicate clicked or not
    */
    public static func setKey(selectedKey : GamePadEnum, clicked : Bool) {
        keysAreDown[selectedKey.rawValue] = clicked;
    }
    
    
}