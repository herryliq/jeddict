/**
 * Copyright [2016] Gaurav Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.netbeans.db.modeler.spec;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jpa.modeler.spec.AssociationOverride;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.jpa.modeler.spec.JoinColumn;
import org.netbeans.jpa.modeler.spec.OneToMany;
import org.netbeans.jpa.modeler.spec.PrimaryKeyJoinColumn;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.MapKeyHandler;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jpa.modeler.spec.extend.SingleRelationAttribute;

/**
 *
 * @author Gaurav Gupta
 */
public class JoinColumnFinder {

    public static List<JoinColumn> findJoinColumns(Attribute attribute, boolean relationTableExist, boolean inverse) {
        return findJoinColumns(attribute, relationTableExist, inverse, false);
    }
    
    public static List<JoinColumn> findMapKeyJoinColumns(Attribute attribute) {
        return findJoinColumns(attribute, false, false, true);
    }
    
    private static List<JoinColumn> findJoinColumns(Attribute attribute, boolean relationTableExist, boolean inverse, boolean mapKey) {
        List<JoinColumn> joinColumns;
        if (mapKey) {
            if (attribute instanceof MapKeyHandler) {
                joinColumns = ((MapKeyHandler) attribute).getMapKeyJoinColumn();
            } else {
                throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
            }
        } else {
            if (attribute instanceof RelationAttribute) {
                if (!relationTableExist) {
                    if (attribute instanceof SingleRelationAttribute) {
                        joinColumns = ((SingleRelationAttribute) attribute).getJoinColumn();
                    } else if (attribute instanceof OneToMany) {
                        joinColumns = ((OneToMany) attribute).getJoinColumn();
                    } else {
                        throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
                    }
                } else if (inverse) {
                    joinColumns = ((RelationAttribute) attribute).getJoinTable().getInverseJoinColumn();
                } else {
                    joinColumns = ((RelationAttribute) attribute).getJoinTable().getJoinColumn();
                }
            } else if (attribute instanceof ElementCollection) { // not applicable for inverse-join-column
                joinColumns = ((ElementCollection) attribute).getCollectionTable().getJoinColumn();
            } else {
                throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
            }
        }
        return joinColumns;
    }

    public static List<JoinColumn> findJoinColumns(AssociationOverride associationOverride, Attribute attribute, boolean relationTableExist, boolean inverse) {
        List<JoinColumn> joinColumns;
        if (attribute instanceof RelationAttribute) {
            if (!relationTableExist) {
                if (attribute instanceof SingleRelationAttribute | attribute instanceof OneToMany) {
                    joinColumns = associationOverride.getJoinColumn();
                } else {
                    throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
                }
            } else if (inverse) {
                joinColumns = associationOverride.getJoinTable().getInverseJoinColumn();
            } else {
                joinColumns = associationOverride.getJoinTable().getJoinColumn();
            }
        } else {
            throw new IllegalStateException("Invalid attribute type : " + attribute.getClass().getSimpleName());
        }
        return joinColumns;
    }

    public static JoinColumn findJoinColumn(String name, List<JoinColumn> joinColumns) {
     return findJoinColumn(name, joinColumns, false);
    }
    
    public static JoinColumn findMapKeyJoinColumn(String name, List<JoinColumn> joinColumns) {
     return findJoinColumn(name, joinColumns, true);
    }
    private static JoinColumn findJoinColumn(String name, List<JoinColumn> joinColumns, boolean mapKey) {
        JoinColumn joinColumn = null;
        boolean created = false;
        List<JoinColumn> joinColumnList = (List<JoinColumn>)joinColumns; //TODO remove casting
        for (Iterator<? extends JoinColumn> it = joinColumns.iterator(); it.hasNext();) {
            JoinColumn column = it.next();
            if (name.equals(column.getName())) {
                joinColumn = column;
                created = true;
                break;
            } else if (StringUtils.isBlank(column.getName())) {
//                it.remove();
            }
        }

        if (!created) {
            joinColumn = new JoinColumn();
            joinColumn.setImplicitName(name);
            joinColumnList.add(joinColumn);
        }
        return joinColumn;
    }

    public static List<PrimaryKeyJoinColumn> findPrimaryKeyJoinColumns(Entity entity) {
        return entity.getPrimaryKeyJoinColumn();
    }
    
    public static PrimaryKeyJoinColumn findPrimaryKeyJoinColumn(String name, List<PrimaryKeyJoinColumn> joinColumns) {
        PrimaryKeyJoinColumn joinColumn = null;
        boolean created = false;
        for (Iterator<PrimaryKeyJoinColumn> it = joinColumns.iterator(); it.hasNext();) {
            PrimaryKeyJoinColumn column = it.next();
            if (name.equals(column.getName())) {
                joinColumn = column;
                created = true;
                break;
            }
        }

        if (!created) {
            joinColumn = new PrimaryKeyJoinColumn();
            joinColumn.setImplicitName(name);
            joinColumns.add(joinColumn);
        }
        return joinColumn;
    }

}
