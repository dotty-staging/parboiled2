/*
 * Copyright (C) 2009-2013 Mathias Doenitz, Alexander Myltsev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parboiled2.support

import shapeless._
import org.parboiled2.Rule
import shapeless.ops.hlist.ReversePrepend

/*
 * The main ActionOps boilerplate is generated by a custom SBT sourceGenerator.
 * This file only contains support types.
 */

// we want to support the "short case class notation" `... ~> Foo`
// unfortunately the Tree for the function argument to the `apply` overloads above does *not* allow us to inspect the
// function type which is why we capture it separately with this helper type
sealed trait FCapture[T]
object FCapture {
  implicit def apply[T]: FCapture[T] = `n/a`
}

// builds `In` and `Out` types according to this logic:
//  if (R == Unit)
//    In = I, Out = L1 ::: L2
//  else if (R <: HList)
//    In = I, Out = L1 ::: L2 ::: R
//  else if (R <: Rule[I2, O2])
//    In = TailSwitch[I2, L1 ::: L2, I], Out = TailSwitch[L1 ::: L2, I2, O2]
//  else
//    In = I, Out = L1 ::: L2 ::: R :: HNil
sealed trait Join[I <: HList, L1 <: HList, L2 <: HList, R] {
  type In <: HList
  type Out <: HList
}
object Join {
  implicit def join[I <: HList, L1 <: HList, L2 <: HList, R, In0 <: HList, Out0 <: HList](implicit x: Aux[I, L1, L2, R, HNil, In0, Out0]): Join[I, L1, L2, R] { type In = In0; type Out = Out0 } = `n/a`

  sealed trait Aux[I <: HList, L1 <: HList, L2 <: HList, R, Acc <: HList, In <: HList, Out <: HList]
  object Aux extends Aux1 {
    // if R == Unit convert to HNil
    implicit def forUnit[I <: HList, L1 <: HList, L2 <: HList, Acc <: HList, Out <: HList](implicit x: Aux[I, L1, L2, HNil, Acc, I, Out]): Aux[I, L1, L2, Unit, Acc, I, Out] = `n/a`

    // if R <: HList and L1 non-empty move head of L1 to Acc
    implicit def iter1[I <: HList, H, T <: HList, L2 <: HList, R <: HList, Acc <: HList, Out <: HList](implicit x: Aux[I, T, L2, R, H :: Acc, I, Out]): Aux[I, H :: T, L2, R, Acc, I, Out] = `n/a`

    // if R <: HList and L1 empty and L2 non-empty move head of L2 to Acc
    implicit def iter2[I <: HList, H, T <: HList, R <: HList, Acc <: HList, Out <: HList](implicit x: Aux[I, HNil, T, R, H :: Acc, I, Out]): Aux[I, HNil, H :: T, R, Acc, I, Out] = `n/a`

    // if R <: HList and L1 and L2 empty set Out = reversePrepend Acc before R
    implicit def terminate[I <: HList, R <: HList, Acc <: HList, Out <: HList](implicit x: ReversePrepend.Aux[Acc, R, Out]): Aux[I, HNil, HNil, R, Acc, I, Out] = `n/a`

    // if R <: Rule and L1 non-empty move head of L1 to Acc
    implicit def iterRule1[I <: HList, L2 <: HList, I2 <: HList, O2 <: HList, In0 <: HList, Acc <: HList, Out0 <: HList, H, T <: HList](implicit x: Aux[I, T, L2, Rule[I2, O2], H :: Acc, In0, Out0]): Aux[I, H :: T, L2, Rule[I2, O2], HNil, In0, Out0] = `n/a`

    // if R <: Rule and L1 empty and Acc non-empty move head of Acc to L2
    implicit def iterRule2[I <: HList, L2 <: HList, I2 <: HList, O2 <: HList, In0 <: HList, Out0 <: HList, H, T <: HList](implicit x: Aux[I, HNil, H :: L2, Rule[I2, O2], T, In0, Out0]): Aux[I, HNil, L2, Rule[I2, O2], H :: T, In0, Out0] = `n/a`

    // if R <: Rule and L1 and Acc empty set In and Out to tailswitches result
    implicit def terminateRule[I <: HList, O <: HList, I2 <: HList, O2 <: HList, In <: HList, Out <: HList](implicit i: TailSwitch.Aux[I2, I2, O, O, I, HNil, In], o: TailSwitch.Aux[O, O, I2, I2, O2, HNil, Out]): Aux[I, HNil, O, Rule[I2, O2], HNil, In, Out] = `n/a`
  }
  abstract class Aux1 {
    // convert R to R :: HNil
    implicit def forAny[I <: HList, L1 <: HList, L2 <: HList, R, Acc <: HList, Out <: HList](implicit x: Aux[I, L1, L2, R :: HNil, Acc, I, Out]): Aux[I, L1, L2, R, Acc, I, Out] = `n/a`
  }
}
