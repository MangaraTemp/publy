/*
 * Copyright 2013 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package publy.data.category.conditions;

import publy.data.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public abstract class Condition {

    private boolean inverted;

    public Condition(boolean inverted) {
        this.inverted = inverted;
    }
    
    /**
     * Checks whether the given item matches this condition.
     * 
     * @param item
     * @return true iff the given item matches this condition.
     */
    public boolean matches(BibItem item) {
        boolean match = internalMatches(item);
        return (inverted ? !match : match);
    }
    
    protected abstract boolean internalMatches(BibItem item);

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
}
