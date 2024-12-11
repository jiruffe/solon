/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.validation.annotation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.util.StringUtils;
import org.noear.solon.validation.Validator;

/**
 *
 * @author noear
 * @since 1.0
 * */
public class NumericValidator implements Validator<Numeric> {
    public static final NumericValidator instance = new NumericValidator();

    @Override
    public String message(Numeric anno) {
        return anno.message();
    }

    @Override
    public Class<?>[] groups(Numeric anno) {
        return anno.groups();
    }

    @Override
    public boolean supportValueType(Class<?> type) {
        return String.class.isAssignableFrom(type);
    }

    @Override
    public Result validateOfValue(Numeric anno, Object val0, StringBuilder tmp) {
        if (val0 != null && val0 instanceof String == false) {
            return Result.failure();
        }

        String val = (String) val0;

        if (verify(anno, val) == false) {
            return Result.failure();
        } else {
            return Result.succeed();
        }
    }

    @Override
    public Result validateOfContext(Context ctx, Numeric anno, String name, StringBuilder tmp) {
        if (name == null) {
            //来自函数
            for (String key : anno.value()) {
                String val = ctx.param(key);

                if (verify(anno, val) == false) {
                    tmp.append(',').append(key);
                }
            }
        } else {
            //来自参数
            String val = ctx.param(name);

            if (verify(anno, val) == false) {
                tmp.append(',').append(name);
            }
        }

        if (tmp.length() > 1) {
            return Result.failure(tmp.substring(1));
        } else {
            return Result.succeed();
        }
    }

    private boolean verify(Numeric anno, String val) {
        //如果为空，算通过（交由@NotEmpty之类，进一步控制）
        if (Utils.isEmpty(val)) {
            return true;
        }

        return StringUtils.isNumber(val);
    }
}
