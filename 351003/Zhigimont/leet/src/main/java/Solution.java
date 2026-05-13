import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solution {
    public static void main(String[] args) {
        String s = "aab";
        System.out.println(lengthOfLongestSubstring(s));
    }


    public static int lengthOfLongestSubstring(String s) {
        int maxLength = 0;
        int currLength = 0;
        int toSkip = 0;
        List<Character> charsInSeq = new ArrayList<>();
        for(int i = 0; i < s.length(); i++){
            charsInSeq.add(s.charAt(i));
            currLength++;
            for(int j = i + 1; j < s.length(); j++){
                if (!charsInSeq.contains(s.charAt(j))){
                    charsInSeq.add(s.charAt(j));
                    currLength++;
                } else {
                    toSkip = charsInSeq.indexOf(s.charAt(j));
                    charsInSeq.clear();
                    break;
                }
            }
            i += toSkip;
            maxLength = Math.max(maxLength, currLength);
            currLength = 0;
        }

        return maxLength;
    }

}
