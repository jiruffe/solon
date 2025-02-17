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
package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.dso.EntityConfig;

/**
 * @author noear 2021/9/27 created
 */
@SolonTest(App.class)
public class EntityConfigTest {

    @Inject
    EntityConfig entityConfig;

    @Test
    public void test(){
        assert entityConfig != null;
        assert entityConfig.codes != null;
        assert entityConfig.codes.size() == 2;
        assert entityConfig.likes != null;
        assert entityConfig.likes.size() == 2;
    }
}
