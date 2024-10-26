package org.noear.solon.data.sqlink.core.dialect;

import org.noear.solon.data.sqlink.base.IDialect;

public class MySQLDialect implements IDialect
{
    @Override
    public String disambiguation(String property)
    {
        return "`" + property + "`";
    }
}
