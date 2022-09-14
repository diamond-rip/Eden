package rip.diamond.practice.util.nametags.construct;

import com.google.common.primitives.Ints;
import rip.diamond.practice.util.nametags.provider.NameTagProvider;

import java.util.Comparator;

public class NameTagComparator implements Comparator<NameTagProvider> {

    public int compare(NameTagProvider a,NameTagProvider b) {
        return Ints.compare(b.getWeight(), a.getWeight());
    }

}
