/*
 *  * Copyright 2016 Skymind, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 */

package org.datavec.spark.transform.quality.integer;

import lombok.AllArgsConstructor;
import org.apache.spark.api.java.function.Function2;
import org.datavec.api.io.data.NullWritable;
import org.datavec.api.io.data.Text;
import org.datavec.api.writable.Writable;
import org.datavec.api.transform.metadata.IntegerMetaData;
import org.datavec.api.transform.dataquality.columns.IntegerQuality;

/**
 * Created by Alex on 5/03/2016.
 */
@AllArgsConstructor
public class IntegerQualityAddFunction implements Function2<IntegerQuality, Writable, IntegerQuality> {

    private final IntegerMetaData meta;

    @Override
    public IntegerQuality call(IntegerQuality v1, Writable writable) throws Exception {

        long valid = v1.getCountValid();
        long invalid = v1.getCountInvalid();
        long countMissing = v1.getCountMissing();
        long countTotal = v1.getCountTotal() + 1;
        long nonInteger = v1.getCountNonInteger();

        if (meta.isValid(writable)) valid++;
        else if (writable instanceof NullWritable || writable instanceof Text && (writable.toString() == null || writable.toString().isEmpty()))
            countMissing++;
        else invalid++;

        String str = writable.toString();
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            nonInteger++;
        }

        return new IntegerQuality(valid, invalid, countMissing, countTotal, nonInteger);
    }
}