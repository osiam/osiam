package org.osiam.storage.helper;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

import java.io.IOException;
import java.util.Properties;

public class UUIDCustomType extends AbstractSingleColumnStandardBasicType {

    private static final long serialVersionUID = -2830983308333068132L;


    private static final SqlTypeDescriptor SQL_DESCRIPTOR;
    private static final JavaTypeDescriptor TYPE_DESCRIPTOR;

    static {
        Properties properties = new Properties();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            properties.load(loader.getResourceAsStream("osiam.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Could not load properties!", e);// NOSONAR - This class probably will be obsolete when the User id will become a String type instead of UUID
        }

        String driver = properties.getProperty("db.driver");
        if (driver.equals("org.postgresql.Driver")) {
            SQL_DESCRIPTOR = PostgresUUIDType.PostgresUUIDSqlTypeDescriptor.INSTANCE;
        } else {
            SQL_DESCRIPTOR = VarcharTypeDescriptor.INSTANCE;
        }

        TYPE_DESCRIPTOR = UUIDTypeDescriptor.INSTANCE;
    }


    public UUIDCustomType() {
        super(SQL_DESCRIPTOR, TYPE_DESCRIPTOR);
    }

    @Override
    public String getName() {
        return "uuid-custom-type";
    }

}
