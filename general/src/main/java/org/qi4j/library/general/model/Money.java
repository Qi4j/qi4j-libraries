/*
 * Copyright (c) 2007, Sianny Halim. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.library.general.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.io.Serializable;

/**
 * Generic interface for Money which stores an amount and currency.
 * Both amount and currency must be immutable.
 */
public interface Money extends Serializable
{
    BigDecimal getAmount();

    // TODO: Amount should be immutable
    void setAmount( BigDecimal amount );

    // TODO: Currency should be immutable
    void setCurrency( Currency currency );

    Currency getCurrency();
}