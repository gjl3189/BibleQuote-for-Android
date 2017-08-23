/*
 * Copyright (C) 2011 Scripture Software
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Project: BibleQuote-for-Android
 * File: MultiThreadSearchProcessorTest.java
 *
 * Created by Vladimir Yakushev at 8/2017
 * E-mail: ru.phoenix@gmail.com
 * WWW: http://www.scripturesoftware.org
 */

package com.BibleQuote.domain.search;

import com.BibleQuote.domain.entity.Module;
import com.BibleQuote.domain.exceptions.BookNotFoundException;
import com.BibleQuote.domain.repository.IModuleRepository;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MultiThreadSearchProcessorTest {

    private MultiThreadSearchProcessor<String, Module> searchProcessor;
    private Module module;
    private final String testContentGen = "<h4>1</h4>\n" +
            "<p><sup>1</sup> В начале сотворил Бог небо и землю.\n" +
            "<p><sup>2</sup> Земля же была безвидна и пуста, и тьма над бездною, и Дух Божий носился над водою.\n" +
            "<p><sup>3</sup> И сказал Бог: да будет свет. И стал свет.\n" +
            "<p><sup>4</sup> И увидел Бог свет, что он хорош, и отделил Бог свет от тьмы.\n" +
            "<p><sup>5</sup> И назвал Бог свет днем, а тьму ночью. И был вечер, и было утро: день один.\n" +
            "<p><sup>6</sup> И сказал Бог: да будет твердь посреди воды, и да отделяет она воду от воды. <font color='#7a8080'>[И стало так.]</font>\n" +
            "<p><sup>7</sup> И создал Бог твердь, и отделил воду, которая под твердью, от воды, которая над твердью. И стало так.\n" +
            "<p><sup>8</sup> И назвал Бог твердь небом. <font color='#7a8080'>[И увидел Бог, что это хорошо.]</font> И был вечер, и было утро: день второй.\n" +
            "<p><sup>9</sup> И сказал Бог: да соберется вода, которая под небом, в одно место, и да явится суша. И стало так. <font color='#7a8080'>[И собралась вода под небом в свои места, и явилась суша.]</font>\n" +
            "<p><sup>10</sup> И назвал Бог сушу землею, а собрание вод назвал морями. И увидел Бог, что это хорошо.\n" +
            "<p><sup>11</sup> И сказал Бог: да произрастит земля зелень, траву, сеющую семя <font color='#7a8080'>[по роду и по подобию ее, и]</font> дерево плодовитое, приносящее по роду своему плод, в котором семя его на земле. И стало так.\n" +
            "<p><sup>12</sup> И произвела земля зелень, траву, сеющую семя по роду <font color='#7a8080'>[и по подобию]</font> ее, и дерево <font color='#7a8080'>[плодовитое]</font>, приносящее плод, в котором семя его по роду его <font color='#7a8080'>[на земле]</font>. И увидел Бог, что это хорошо.\n" +
            "<p><sup>13</sup> И был вечер, и было утро: день третий.\n" +
            "<p><sup>14</sup> И сказал Бог: да будут светила на тверди небесной <font color='#7a8080'>[для освещения земли и]</font> для отделения дня от ночи, и для знамений, и времен, и дней, и годов;\n" +
            "<p><sup>15</sup> и да будут они светильниками на тверди небесной, чтобы светить на землю. И стало так.\n" +
            "<p><sup>16</sup> И создал Бог два светила великие: светило большее, для управления днем, и светило меньшее, для управления ночью, и звезды;\n" +
            "<p><sup>17</sup> и поставил их Бог на тверди небесной, чтобы светить на землю,\n" +
            "<p><sup>18</sup> и управлять днем и ночью, и отделять свет от тьмы. И увидел Бог, что это хорошо.\n" +
            "<p><sup>19</sup> И был вечер, и было утро: день четвёртый.\n" +
            "<p><sup>20</sup> И сказал Бог: да произведет вода пресмыкающихся, душу живую; и птицы да полетят над землею, по тверди небесной. <font color='#7a8080'>[И стало так.]</font>\n" +
            "<p><sup>21</sup> И сотворил Бог рыб больших и всякую душу животных пресмыкающихся, которых произвела вода, по роду их, и всякую птицу пернатую по роду ее. И увидел Бог, что это хорошо.\n" +
            "<p><sup>22</sup> И благословил их Бог, говоря: плодитесь и размножайтесь, и наполняйте воды в морях, и птицы да размножаются на земле.\n" +
            "<p><sup>23</sup> И был вечер, и было утро: день пятый.\n" +
            "<p><sup>24</sup> И сказал Бог: да произведет земля душу живую по роду ее, скотов, и гадов, и зверей земных по роду их. И стало так.\n" +
            "<p><sup>25</sup> И создал Бог зверей земных по роду их, и скот по роду его, и всех гадов земных по роду их. И увидел Бог, что это хорошо.\n" +
            "<p><sup>26</sup> И сказал Бог: сотворим человека по образу Нашему <font color='#7a8080'>[и]</font> по подобию Нашему, и да владычествуют они над рыбами морскими, и над птицами небесными, <font color='#7a8080'>[и над зверями,]</font> и над скотом, и над всею землею, и над всеми гадами, пресмыкающимися по земле.\n" +
            "<p><sup>27</sup> И сотворил Бог человека по образу Своему, по образу Божию сотворил его; мужчину и женщину сотворил их.\n" +
            "<p><sup>28</sup> И благословил их Бог, и сказал им Бог: плодитесь и размножайтесь, и наполняйте землю, и обладайте ею, и владычествуйте над рыбами морскими <font color='#7a8080'>[и над зверями,]</font> и над птицами небесными, <font color='#7a8080'>[и над всяким скотом, и над всею землею,]</font> и над всяким животным, пресмыкающимся по земле.\n" +
            "<p><sup>29</sup> И сказал Бог: вот, Я дал вам всякую траву, сеющую семя, какая есть на всей земле, и всякое дерево, у которого плод древесный, сеющий семя; - вам сие будет в пищу;\n" +
            "<p><sup>30</sup> а всем зверям земным, и всем птицам небесным, и всякому <font color='#7a8080'>[гаду,]</font> пресмыкающемуся по земле, в котором душа живая, дал Я всю зелень травную в пищу. И стало так.\n" +
            "<p><sup>31</sup> И увидел Бог все, что Он создал, и вот, хорошо весьма. И был вечер, и было утро: день шестой.\n" +
            "<h4>2</h4>\n" +
            "<p><sup>1</sup> Так совершены небо и земля и все воинство их.\n" +
            "<p><sup>2</sup> И совершил Бог к седьмому дню дела Свои, которые Он делал, и почил в день седьмый от всех дел Своих, которые делал.\n" +
            "<p><sup>3</sup> И благословил Бог седьмой день, и освятил его, ибо в оный почил от всех дел Своих, которые Бог творил и созидал.\n" +
            "<p><sup>4</sup> Вот происхождение неба и земли, при сотворении их, в то время, когда Господь Бог создал землю и небо,\n" +
            "<p><sup>5</sup> и всякий полевой кустарник, которого еще не было на земле, и всякую полевую траву, которая еще не росла, ибо Господь Бог не посылал дождя на землю, и не было человека для возделывания земли,\n" +
            "<p><sup>6</sup> но пар поднимался с земли и орошал все лице земли.\n" +
            "<p><sup>7</sup> И создал Господь Бог человека из праха земного, и вдунул в лице его дыхание жизни, и стал человек душею живою.\n" +
            "<p><sup>8</sup> И насадил Господь Бог рай в Едеме на востоке, и поместил там человека, которого создал.\n" +
            "<p><sup>9</sup> И произрастил Господь Бог из земли всякое дерево, приятное на вид и хорошее для пищи, и дерево жизни посреди рая, и дерево познания добра и зла.\n" +
            "<p><sup>10</sup> Из Едема выходила река для орошения рая; и потом разделялась на четыре реки.\n" +
            "<p><sup>11</sup> Имя одной Фисон: она обтекает всю землю Хавила, ту, где золото;\n" +
            "<p><sup>12</sup> и золото той земли хорошее; там бдолах и камень оникс.\n" +
            "<p><sup>13</sup> Имя второй реки Гихон <font color='#7a8080'>[Геон]</font>: она обтекает всю землю Куш.\n" +
            "<p><sup>14</sup> Имя третьей реки Хиддекель <font color='#7a8080'>[Тигр]</font>: она протекает пред Ассириею. Четвертая река Евфрат.\n" +
            "<p><sup>15</sup> И взял Господь Бог человека, <font color='#7a8080'>[которого создал,]</font> и поселил его в саду Едемском, чтобы возделывать его и хранить его.\n" +
            "<p><sup>16</sup> И заповедал Господь Бог человеку, говоря: от всякого дерева в саду ты будешь есть,\n" +
            "<p><sup>17</sup> а от дерева познания добра и зла не ешь от него, ибо в день, в который ты вкусишь от него, смертью умрешь.\n" +
            "<p><sup>18</sup> И сказал Господь Бог: не хорошо быть человеку одному; сотворим ему помощника, соответственного ему.\n" +
            "<p><sup>19</sup> Господь Бог образовал из земли всех животных полевых и всех птиц небесных, и привел <font color='#7a8080'>[их]</font> к человеку, чтобы видеть, как он назовет их, и чтобы, как наречет человек всякую душу живую, так и было имя ей.\n" +
            "<p><sup>20</sup> И нарек человек имена всем скотам и птицам небесным и всем зверям полевым; но для человека не нашлось помощника, подобного ему.\n" +
            "<p><sup>21</sup> И навел Господь Бог на человека крепкий сон; и, когда он уснул, взял одно из ребр его, и закрыл то место плотию.\n" +
            "<p><sup>22</sup> И создал Господь Бог из ребра, взятого у человека, жену, и привел ее к человеку.\n" +
            "<p><sup>23</sup> И сказал человек: вот, это кость от костей моих и плоть от плоти моей; она будет называться женою, ибо взята от мужа <font color='#7a8080'>[своего]</font>.\n" +
            "<p><sup>24</sup> Потому оставит человек отца своего и мать свою и прилепится к жене своей; и будут <font color='#7a8080'>[два]</font> одна плоть.\n" +
            "<p><sup>25</sup> И были оба наги, Адам и жена его, и не стыдились.\n" +
            "<h4>3</h4>\n" +
            "<p><sup>1</sup> Змей был хитрее всех зверей полевых, которых создал Господь Бог. И сказал змей жене: подлинно ли сказал Бог: не ешьте ни от какого дерева в раю?\n" +
            "<p><sup>2</sup> И сказала жена змею: плоды с дерев мы можем есть,\n" +
            "<p><sup>3</sup> только плодов дерева, которое среди рая, сказал Бог, не ешьте их и не прикасайтесь к ним, чтобы вам не умереть.\n" +
            "<p><sup>4</sup> И сказал змей жене: нет, не умрете,\n" +
            "<p><sup>5</sup> но знает Бог, что в день, в который вы вкусите их, откроются глаза ваши, и вы будете, как боги, знающие добро и зло.\n" +
            "<p><sup>6</sup> И увидела жена, что дерево хорошо для пищи, и что оно приятно для глаз и вожделенно, потому что дает знание; и взяла плодов его и ела; и дала также мужу своему, и он ел.\n" +
            "<p><sup>7</sup> И открылись глаза у них обоих, и узнали они, что наги, и сшили смоковные листья, и сделали себе опоясания.\n" +
            "<p><sup>8</sup> И услышали голос Господа Бога, ходящего в раю во время прохлады дня; и скрылся Адам и жена его от лица Господа Бога между деревьями рая.\n" +
            "<p><sup>9</sup> И воззвал Господь Бог к Адаму и сказал ему: <font color='#7a8080'>[Адам,]</font> где ты?\n" +
            "<p><sup>10</sup> Он сказал: голос Твой я услышал в раю, и убоялся, потому что я наг, и скрылся.\n" +
            "<p><sup>11</sup> И сказал <font color='#7a8080'>[Бог]</font>: кто сказал тебе, что ты наг? не ел ли ты от дерева, с которого Я запретил тебе есть?\n" +
            "<p><sup>12</sup> Адам сказал: жена, которую Ты мне дал, она дала мне от дерева, и я ел.\n" +
            "<p><sup>13</sup> И сказал Господь Бог жене: что ты это сделала? Жена сказала: змей обольстил меня, и я ела.\n" +
            "<p><sup>14</sup> И сказал Господь Бог змею: за то, что ты сделал это, проклят ты пред всеми скотами и пред всеми зверями полевыми; ты будешь ходить на чреве твоем, и будешь есть прах во все дни жизни твоей;\n" +
            "<p><sup>15</sup> и вражду положу между тобою и между женою, и между семенем твоим и между семенем ее; оно будет поражать тебя в голову, а ты будешь жалить его в пяту * <font color='#7a8080'>[(* По другому чтению: и между Семенем ее; Он будет поражать тебя в голову, а ты будешь жалить Его в пяту. - Прим. ред.)]</font>.\n" +
            "<p><sup>16</sup> Жене сказал: умножая умножу скорбь твою в беременности твоей; в болезни будешь рождать детей; и к мужу твоему влечение твое, и он будет господствовать над тобою.\n" +
            "<p><sup>17</sup> Адаму же сказал: за то, что ты послушал голоса жены твоей и ел от дерева, о котором Я заповедал тебе, сказав: не ешь от него, проклята земля за тебя; со скорбью будешь питаться от нее во все дни жизни твоей;\n" +
            "<p><sup>18</sup> терния и волчцы произрастит она тебе; и будешь питаться полевою травою;\n" +
            "<p><sup>19</sup> в поте лица твоего будешь есть хлеб, доколе не возвратишься в землю, из которой ты взят, ибо прах ты и в прах возвратишься.\n" +
            "<p><sup>20</sup> И нарек Адам имя жене своей: Ева * <font color='#7a8080'>[(* Жизнь. - Прим. ред.)]</font>, ибо она стала матерью всех живущих.\n" +
            "<p><sup>21</sup> И сделал Господь Бог Адаму и жене его одежды кожаные и одел их.\n" +
            "<p><sup>22</sup> И сказал Господь Бог: вот, Адам стал как один из Нас, зная добро и зло; и теперь как бы не простер он руки своей, и не взял также от дерева жизни, и не вкусил, и не стал жить вечно.\n" +
            "<p><sup>23</sup> И выслал его Господь Бог из сада Едемского, чтобы возделывать землю, из которой он взят.\n" +
            "<p><sup>24</sup> И изгнал Адама, и поставил на востоке у сада Едемского Херувима и пламенный меч обращающийся, чтобы охранять путь к дереву жизни.\n" +
            "<h4>4</h4>\n" +
            "<p><sup>1</sup> Адам познал Еву, жену свою; и она зачала, и родила Каина, и сказала: приобрела я человека от Господа.\n" +
            "<p><sup>2</sup> И еще родила брата его, Авеля. И был Авель пастырь овец, а Каин был земледелец.\n" +
            "<p><sup>3</sup> Спустя несколько времени, Каин принес от плодов земли дар Господу,\n" +
            "<p><sup>4</sup> и Авель также принес от первородных стада своего и от тука их. И призрел Господь на Авеля и на дар его,\n" +
            "<p><sup>5</sup> а на Каина и на дар его не призрел. Каин сильно огорчился, и поникло лице его.\n" +
            "<p><sup>6</sup> И сказал Господь <font color='#7a8080'>[Бог]</font> Каину: почему ты огорчился? и отчего поникло лице твое?\n" +
            "<p><sup>7</sup> если делаешь доброе, то не поднимаешь ли лица? а если не делаешь доброго, то у дверей грех лежит; он влечет тебя к себе, но ты господствуй над ним.\n" +
            "<p><sup>8</sup> И сказал Каин Авелю, брату своему: <font color='#7a8080'>[пойдем в поле]</font>. И когда они были в поле, восстал Каин на Авеля, брата своего, и убил его.\n" +
            "<p><sup>9</sup> И сказал Господь <font color='#7a8080'>[Бог]</font> Каину: где Авель, брат твой? Он сказал: не знаю; разве я сторож брату моему?\n" +
            "<p><sup>10</sup> И сказал <font color='#7a8080'>[Господь]</font>: что ты сделал? голос крови брата твоего вопиет ко Мне от земли;\n" +
            "<p><sup>11</sup> и ныне проклят ты от земли, которая отверзла уста свои принять кровь брата твоего от руки твоей;\n" +
            "<p><sup>12</sup> когда ты будешь возделывать землю, она не станет более давать силы своей для тебя; ты будешь изгнанником и скитальцем на земле.\n" +
            "<p><sup>13</sup> И сказал Каин Господу <font color='#7a8080'>[Богу]</font>: наказание мое больше, нежели снести можно;\n" +
            "<p><sup>14</sup> вот, Ты теперь сгоняешь меня с лица земли, и от лица Твоего я скроюсь, и буду изгнанником и скитальцем на земле; и всякий, кто встретится со мною, убьет меня.\n" +
            "<p><sup>15</sup> И сказал ему Господь <font color='#7a8080'>[Бог]</font>: за то всякому, кто убьет Каина, отмстится всемеро. И сделал Господь <font color='#7a8080'>[Бог]</font> Каину знамение, чтобы никто, встретившись с ним, не убил его.\n" +
            "<p><sup>16</sup> И пошел Каин от лица Господня и поселился в земле Нод, на восток от Едема.\n" +
            "<p><sup>17</sup> И познал Каин жену свою; и она зачала и родила Еноха. И построил он город; и назвал город по имени сына своего: Енох.\n" +
            "<p><sup>18</sup> У Еноха родился Ирад <font color='#7a8080'>[Гаидад]</font>; Ирад родил Мехиаеля <font color='#7a8080'>[Малелеила]</font>; Мехиаель родил Мафусала; Мафусал родил Ламеха.\n" +
            "<p><sup>19</sup> И взял себе Ламех две жены: имя одной: Ада, и имя второй: Цилла <font color='#7a8080'>[Селла]</font>.\n" +
            "<p><sup>20</sup> Ада родила Иавала: он был отец живущих в шатрах со стадами.\n" +
            "<p><sup>21</sup> Имя брату его Иувал: он был отец всех играющих на гуслях и свирели.\n" +
            "<p><sup>22</sup> Цилла также родила Тувалкаина <font color='#7a8080'>[Фовела]</font>, который был ковачом всех орудий из меди и железа. И сестра Тувалкаина Ноема.\n" +
            "<p><sup>23</sup> И сказал Ламех женам своим: Ада и Цилла! послушайте голоса моего; жены Ламеховы! внимайте словам моим: я убил мужа в язву мне и отрока в рану мне;\n" +
            "<p><sup>24</sup> если за Каина отмстится всемеро, то за Ламеха в семьдесят раз всемеро.\n" +
            "<p><sup>25</sup> И познал Адам еще <font color='#7a8080'>[Еву,]</font> жену свою, и она родила сына, и нарекла ему имя: Сиф, потому что, <font color='#7a8080'>[говорила она,]</font> Бог положил мне другое семя, вместо Авеля, которого убил Каин.\n" +
            "<p><sup>26</sup> У Сифа также родился сын, и он нарек ему имя: Енос; тогда начали призывать имя Господа <font color='#7a8080'>[Бога]</font>.\n";
    private final String testContentExo = "<h4>1</h4>\n" +
            "<p><sup>1</sup> Вот имена сынов Израилевых, которые вошли в Египет с Иаковом <font color='#7a8080'>[отцом их]</font>, вошли каждый со <font color='#7a8080'>[всем]</font> домом своим:\n" +
            "<p><sup>2</sup> Рувим, Симеон, Левий и Иуда,\n" +
            "<p><sup>3</sup> Иссахар, Завулон и Вениамин,\n" +
            "<p><sup>4</sup> Дан и Неффалим, Гад и Асир.\n" +
            "<p><sup>5</sup> Всех же душ, происшедших от чресл Иакова, было семьдесят <font color='#7a8080'>[пять]</font>, а Иосиф был уже в Египте.\n" +
            "<p><sup>6</sup> И умер Иосиф и все братья его и весь род их;\n" +
            "<p><sup>7</sup> а сыны Израилевы расплодились и размножились, и возросли и усилились чрезвычайно, и наполнилась ими земля та.\n" +
            "<p><sup>8</sup> И восстал в Египте новый царь, который не знал Иосифа,\n" +
            "<p><sup>9</sup> и сказал народу своему: вот, народ сынов Израилевых многочислен и сильнее нас;\n" +
            "<p><sup>10</sup> перехитрим же его, чтобы он не размножался; иначе, когда случится война, соединится и он с нашими неприятелями, и вооружится против нас, и выйдет из земли <font color='#7a8080'>[нашей]</font>.\n" +
            "<p><sup>11</sup> И поставили над ним начальников работ, чтобы изнуряли его тяжкими работами. И он построил фараону Пифом и Раамсес, города для запасов, <font color='#7a8080'>[и Он, иначе Илиополь]</font>.\n" +
            "<p><sup>12</sup> Но чем более изнуряли его, тем более он умножался и тем более возрастал, так что <font color='#7a8080'>[Египтяне]</font> опасались сынов Израилевых.\n" +
            "<p><sup>13</sup> И потому Египтяне с жестокостью принуждали сынов Израилевых к работам\n" +
            "<p><sup>14</sup> и делали жизнь их горькою от тяжкой работы над глиною и кирпичами и от всякой работы полевой, от всякой работы, к которой принуждали их с жестокостью.\n" +
            "<p><sup>15</sup> Царь Египетский повелел повивальным бабкам Евреянок, из коих одной имя Шифра, а другой Фуа,\n" +
            "<p><sup>16</sup> и сказал <font color='#7a8080'>[им]</font>: когда вы будете повивать у Евреянок, то наблюдайте при родах: если будет сын, то умерщвляйте его, а если дочь, то пусть живет.\n" +
            "<p><sup>17</sup> Но повивальные бабки боялись Бога и не делали так, как говорил им царь Египетский, и оставляли детей в живых.\n" +
            "<p><sup>18</sup> Царь Египетский призвал повивальных бабок и сказал им: для чего вы делаете такое дело, что оставляете детей в живых?\n" +
            "<p><sup>19</sup> Повивальные бабки сказали фараону: Еврейские женщины не так, как Египетские; они здоровы, ибо прежде нежели придет к ним повивальная бабка, они уже рождают.\n" +
            "<p><sup>20</sup> За сие Бог делал добро повивальным бабкам, а народ умножался и весьма усиливался.\n" +
            "<p><sup>21</sup> И так как повивальные бабки боялись Бога, то Он устроял домы их.\n" +
            "<p><sup>22</sup> Тогда фараон всему народу своему повелел, говоря: всякого новорожденного <font color='#7a8080'>[у Евреев]</font> сына бросайте в реку, а всякую дочь оставляйте в живых.\n" +
            "<h4>2</h4>\n" +
            "<p><sup>1</sup> Некто из племени Левиина пошел и взял себе жену из того же племени.\n" +
            "<p><sup>2</sup> Жена зачала и родила сына и, видя, что он очень красив, скрывала его три месяца;\n" +
            "<p><sup>3</sup> но не могши долее скрывать его, взяла корзинку из тростника и осмолила ее асфальтом и смолою и, положив в нее младенца, поставила в тростнике у берега реки,\n" +
            "<p><sup>4</sup> а сестра его стала вдали наблюдать, что с ним будет.\n" +
            "<p><sup>5</sup> И вышла дочь фараонова на реку мыться, а прислужницы ее ходили по берегу реки. Она увидела корзинку среди тростника и послала рабыню свою взять ее.\n" +
            "<p><sup>6</sup> Открыла и увидела младенца; и вот, дитя плачет <font color='#7a8080'>[в корзинке]</font>; и сжалилась над ним <font color='#7a8080'>[дочь фараонова]</font> и сказала: это из Еврейских детей.\n" +
            "<p><sup>7</sup> И сказала сестра его дочери фараоновой: не сходить ли мне и не позвать ли к тебе кормилицу из Евреянок, чтоб она вскормила тебе младенца?\n" +
            "<p><sup>8</sup> Дочь фараонова сказала ей: сходи. Девица пошла и призвала мать младенца.\n" +
            "<p><sup>9</sup> Дочь фараонова сказала ей: возьми младенца сего и вскорми его мне; я дам тебе плату. Женщина взяла младенца и кормила его.\n" +
            "<p><sup>10</sup> И вырос младенец, и она привела его к дочери фараоновой, и он был у нее вместо сына, и нарекла имя ему: Моисей, потому что, говорила она, я из воды вынула его.\n" +
            "<p><sup>11</sup> Спустя много времени, когда Моисей вырос, случилось, что он вышел к братьям своим <font color='#7a8080'>[сынам Израилевым]</font> и увидел тяжкие работы их; и увидел, что Египтянин бьет одного Еврея из братьев его, <font color='#7a8080'>[сынов Израилевых]</font>.\n" +
            "<p><sup>12</sup> Посмотрев туда и сюда и видя, что нет никого, он убил Египтянина и скрыл его в песке.\n" +
            "<p><sup>13</sup> И вышел он на другой день, и вот, два Еврея ссорятся; и сказал он обижающему: зачем ты бьешь ближнего твоего?\n" +
            "<p><sup>14</sup> А тот сказал: кто поставил тебя начальником и судьею над нами? не думаешь ли убить меня, как убил <font color='#7a8080'>[вчера]</font> Египтянина? Моисей испугался и сказал: верно, узнали об этом деле.\n" +
            "<p><sup>15</sup> И услышал фараон об этом деле и хотел убить Моисея; но Моисей убежал от фараона и остановился в земле Мадиамской, и <font color='#7a8080'>[придя в землю Мадиамскую]</font> сел у колодезя.\n" +
            "<p><sup>16</sup> У священника Мадиамского <font color='#7a8080'>[было]</font> семь дочерей, <font color='#7a8080'>[которые пасли овец отца своего Иофора]</font>. Они пришли, начерпали воды и наполнили корыта, чтобы напоить овец отца своего <font color='#7a8080'>[Иофора]</font>.\n" +
            "<p><sup>17</sup> И пришли пастухи и отогнали их. Тогда встал Моисей и защитил их, <font color='#7a8080'>[и начерпал им воды]</font> и напоил овец их.\n" +
            "<p><sup>18</sup> И пришли они к Рагуилу, отцу своему, и он сказал <font color='#7a8080'>[им]</font>: что вы так скоро пришли сегодня?\n" +
            "<p><sup>19</sup> Они сказали: какой-то Египтянин защитил нас от пастухов, и даже начерпал нам воды и напоил овец <font color='#7a8080'>[наших]</font>.\n" +
            "<p><sup>20</sup> Он сказал дочерям своим: где же он? зачем вы его оставили? позовите его, и пусть он ест хлеб.\n" +
            "<p><sup>21</sup> Моисею понравилось жить у сего человека; и он выдал за Моисея дочь свою Сепфору.\n" +
            "<p><sup>22</sup> Она <font color='#7a8080'>[зачала и]</font> родила сына, и <font color='#7a8080'>[Моисей]</font> нарек ему имя: Гирсам, потому что, говорил он, я стал пришельцем в чужой земле. <font color='#7a8080'>[И зачав еще, родила другого сына, и он нарек ему имя: Елиезер, сказав: Бог отца моего был мне помощником и избавил меня от руки фараона.]</font>\n" +
            "<p><sup>23</sup> Спустя долгое время, умер царь Египетский. И стенали сыны Израилевы от работы и вопияли, и вопль их от работы восшел к Богу.\n" +
            "<p><sup>24</sup> И услышал Бог стенание их, и вспомнил Бог завет Свой с Авраамом, Исааком и Иаковом.\n" +
            "<p><sup>25</sup> И увидел Бог сынов Израилевых, и призрел их Бог.\n" +
            "<h4>3</h4>\n" +
            "<p><sup>1</sup> Моисей пас овец у Иофора, тестя своего, священника Мадиамского. Однажды провел он стадо далеко в пустыню и пришел к горе Божией, Хориву.\n" +
            "<p><sup>2</sup> И явился ему Ангел Господень в пламени огня из среды тернового куста. И увидел он, что терновый куст горит огнем, но куст не сгорает.\n" +
            "<p><sup>3</sup> Моисей сказал: пойду и посмотрю на сие великое явление, отчего куст не сгорает.\n" +
            "<p><sup>4</sup> Господь увидел, что он идет смотреть, и воззвал к нему Бог из среды куста, и сказал: Моисей! Моисей! Он сказал: вот я, <font color='#7a8080'>[Господи]</font>!\n" +
            "<p><sup>5</sup> И сказал Бог: не подходи сюда; сними обувь твою с ног твоих, ибо место, на котором ты стоишь, есть земля святая.\n" +
            "<p><sup>6</sup> И сказал <font color='#7a8080'>[ему]</font>: Я Бог отца твоего, Бог Авраама, Бог Исаака и Бог Иакова. Моисей закрыл лице свое, потому что боялся воззреть на Бога.\n" +
            "<p><sup>7</sup> И сказал Господь <font color='#7a8080'>[Моисею]</font>: Я увидел страдание народа Моего в Египте и услышал вопль его от приставников его; Я знаю скорби его\n" +
            "<p><sup>8</sup> и иду избавить его от руки Египтян и вывести его из земли сей <font color='#7a8080'>[и ввести его]</font> в землю хорошую и пространную, где течет молоко и мед, в землю Хананеев, Хеттеев, Аморреев, Ферезеев, <font color='#7a8080'>[Гергесеев,]</font> Евеев и Иевусеев.\n" +
            "<p><sup>9</sup> И вот, уже вопль сынов Израилевых дошел до Меня, и Я вижу угнетение, каким угнетают их Египтяне.\n" +
            "<p><sup>10</sup> Итак пойди: Я пошлю тебя к фараону <font color='#7a8080'>[царю Египетскому]</font>; и выведи из Египта народ Мой, сынов Израилевых.\n" +
            "<p><sup>11</sup> Моисей сказал Богу: кто я, чтобы мне идти к фараону <font color='#7a8080'>[царю Египетскому]</font> и вывести из Египта сынов Израилевых?\n" +
            "<p><sup>12</sup> И сказал <font color='#7a8080'>[Бог]</font>: Я буду с тобою, и вот тебе знамение, что Я послал тебя: когда ты выведешь народ <font color='#7a8080'>[Мой]</font> из Египта, вы совершите служение Богу на этой горе.\n" +
            "<p><sup>13</sup> И сказал Моисей Богу: вот, я приду к сынам Израилевым и скажу им: Бог отцов ваших послал меня к вам. А они скажут мне: как Ему имя? Что сказать мне им?\n" +
            "<p><sup>14</sup> Бог сказал Моисею: Я есмь Сущий. И сказал: так скажи сынам Израилевым: Сущий <font color='#7a8080'>[Иегова]</font> послал меня к вам.\n" +
            "<p><sup>15</sup> И сказал еще Бог Моисею: так скажи сынам Израилевым: Господь, Бог отцов ваших, Бог Авраама, Бог Исаака и Бог Иакова послал меня к вам. Вот имя Мое на веки, и памятование о Мне из рода в род.\n" +
            "<p><sup>16</sup> Пойди, собери старейшин <font color='#7a8080'>[сынов]</font> Израилевых и скажи им: Господь, Бог отцов ваших, явился мне, Бог Авраама, <font color='#7a8080'>[Бог]</font> Исаака и <font color='#7a8080'>[Бог]</font> Иакова, и сказал: Я посетил вас и увидел, что делается с вами в Египте.\n" +
            "<p><sup>17</sup> И сказал: Я выведу вас от угнетения Египетского в землю Хананеев, Хеттеев, Аморреев, Ферезеев, <font color='#7a8080'>[Гергесеев,]</font> Евеев и Иевусеев, в землю, где течет молоко и мед.\n" +
            "<p><sup>18</sup> И они послушают голоса твоего, и пойдешь ты и старейшины Израилевы к <font color='#7a8080'>[фараону]</font> царю Египетскому, и скажете ему: Господь, Бог Евреев, призвал нас; итак отпусти нас в пустыню, на три дня пути, чтобы принести жертву Господу, Богу нашему.\n" +
            "<p><sup>19</sup> Но Я знаю, что <font color='#7a8080'>[фараон]</font> царь Египетский не позволит вам идти, если не принудить его рукою крепкою;\n" +
            "<p><sup>20</sup> и простру руку Мою и поражу Египет всеми чудесами Моими, которые сделаю среди его; и после того он отпустит вас.\n" +
            "<p><sup>21</sup> И дам народу сему милость в глазах Египтян; и когда пойдете, то пойдете не с пустыми руками:\n" +
            "<p><sup>22</sup> каждая женщина выпросит у соседки своей и у живущей в доме ее вещей серебряных и вещей золотых, и одежд, и вы нарядите ими и сыновей ваших и дочерей ваших, и оберете Египтян.\n" +
            "<h4>4</h4>\n" +
            "<p><sup>1</sup> И отвечал Моисей и сказал: а если они не поверят мне и не послушают голоса моего и скажут: не явился тебе Господь? <font color='#7a8080'>[что сказать им?]</font>\n" +
            "<p><sup>2</sup> И сказал ему Господь: что это в руке у тебя? Он отвечал: жезл.\n" +
            "<p><sup>3</sup> Господь сказал: брось его на землю. Он бросил его на землю, и жезл превратился в змея, и Моисей побежал от него.\n" +
            "<p><sup>4</sup> И сказал Господь Моисею: простри руку твою и возьми его за хвост. Он простер руку свою, и взял его <font color='#7a8080'>[за хвост]</font>; и он стал жезлом в руке его.\n" +
            "<p><sup>5</sup> Это для того, чтобы поверили <font color='#7a8080'>[тебе]</font>, что явился тебе Господь, Бог отцов их, Бог Авраама, Бог Исаака и Бог Иакова.\n" +
            "<p><sup>6</sup> Еще сказал ему Господь: положи руку твою к себе в пазуху. И он положил руку свою к себе в пазуху, вынул ее <font color='#7a8080'>[из пазухи своей]</font>, и вот, рука его побелела от проказы, как снег.\n" +
            "<p><sup>7</sup> <font color='#7a8080'>[Еще]</font> сказал <font color='#7a8080'>[ему Господь]</font>: положи опять руку твою к себе в пазуху. И он положил руку свою к себе в пазуху; и вынул ее из пазухи своей, и вот, она опять стала такою же, как тело его.\n" +
            "<p><sup>8</sup> Если они не поверят тебе и не послушают голоса первого знамения, то поверят голосу знамения другого;\n" +
            "<p><sup>9</sup> если же не поверят и двум сим знамениям и не послушают голоса твоего, то возьми воды из реки и вылей на сушу; и вода, взятая из реки, сделается кровью на суше.\n" +
            "<p><sup>10</sup> И сказал Моисей Господу: о, Господи! человек я не речистый, и таков был и вчера и третьего дня, и когда Ты начал говорить с рабом Твоим: я тяжело говорю и косноязычен.\n" +
            "<p><sup>11</sup> Господь сказал <font color='#7a8080'>[Моисею]</font>: кто дал уста человеку? кто делает немым, или глухим, или зрячим, или слепым? не Я ли Господь <font color='#7a8080'>[Бог]</font>?\n" +
            "<p><sup>12</sup> итак пойди, и Я буду при устах твоих и научу тебя, что тебе говорить.\n" +
            "<p><sup>13</sup> <font color='#7a8080'>[Моисей]</font> сказал: Господи! пошли другого, кого можешь послать.\n" +
            "<p><sup>14</sup> И возгорелся гнев Господень на Моисея, и Он сказал: разве нет у тебя Аарона брата, Левитянина? Я знаю, что он может говорить <font color='#7a8080'>[вместо тебя]</font>, и вот, он выйдет навстречу тебе, и, увидев тебя, возрадуется в сердце своем;\n" +
            "<p><sup>15</sup> ты будешь ему говорить и влагать слова <font color='#7a8080'>[Мои]</font> в уста его, а Я буду при устах твоих и при устах его и буду учить вас, что вам делать;\n" +
            "<p><sup>16</sup> и будет говорить он вместо тебя к народу; итак он будет твоими устами, а ты будешь ему вместо Бога;\n" +
            "<p><sup>17</sup> и жезл сей <font color='#7a8080'>[который был обращен в змея]</font> возьми в руку твою: им ты будешь творить знамения.\n" +
            "<p><sup>18</sup> И пошел Моисей, и возвратился к Иофору, тестю своему, и сказал ему: пойду я, и возвращусь к братьям моим, которые в Египте, и посмотрю, живы ли еще они? И сказал Иофор Моисею: иди с миром. <font color='#7a8080'>[Спустя много времени умер царь Египетский.]</font>\n" +
            "<p><sup>19</sup> И сказал Господь Моисею в <font color='#7a8080'>[земле]</font> Мадиамской: пойди, возвратись в Египет, ибо умерли все, искавшие души твоей.\n" +
            "<p><sup>20</sup> И взял Моисей жену свою и сыновей своих, посадил их на осла и отправился в землю Египетскую. И жезл Божий Моисей взял в руку свою.\n" +
            "<p><sup>21</sup> И сказал Господь Моисею: когда пойдешь и возвратишься в Египет, смотри, все чудеса, которые Я поручил тебе, сделай пред лицем фараона, а Я ожесточу сердце его, и он не отпустит народа.\n" +
            "<p><sup>22</sup> И скажи фараону: так говорит Господь <font color='#7a8080'>[Бог Еврейский]</font>: Израиль есть сын Мой, первенец Мой;\n" +
            "<p><sup>23</sup> Я говорю тебе: отпусти сына Моего, чтобы он совершил Мне служение; а если не отпустишь его, то вот, Я убью сына твоего, первенца твоего.\n" +
            "<p><sup>24</sup> Дорогою на ночлеге случилось, что встретил его Господь и хотел умертвить его.\n" +
            "<p><sup>25</sup> Тогда Сепфора, взяв каменный нож, обрезала крайнюю плоть сына своего и, бросив к ногам его, сказала: ты жених крови у меня.\n" +
            "<p><sup>26</sup> И отошел от него Господь. Тогда сказала она: жених крови - по обрезанию.\n" +
            "<p><sup>27</sup> И Господь сказал Аарону: пойди навстречу Моисею в пустыню. И он пошел, и встретился с ним при горе Божией, и поцеловал его.\n" +
            "<p><sup>28</sup> И пересказал Моисей Аарону все слова Господа, Который его послал, и все знамения, которые Он заповедал.\n" +
            "<p><sup>29</sup> И пошел Моисей с Аароном, и собрали они всех старейшин сынов Израилевых,\n" +
            "<p><sup>30</sup> и пересказал <font color='#7a8080'>[им]</font> Аарон все слова, которые говорил Господь Моисею; и сделал Моисей знамения пред глазами народа,\n" +
            "<p><sup>31</sup> и поверил народ; и услышали, что Господь посетил сынов Израилевых и увидел страдание их, и преклонились они и поклонились.\n";

    @Before
    public void setUp() throws Exception {
        IModuleRepository<String, Module> repository = mock(MockModuleRepository.class);
        when(repository.getBookContent(any(), eq("Gen"))).thenReturn(testContentGen);
        when(repository.getBookContent(any(), eq("Exo"))).thenReturn(testContentExo);
        when(repository.getBookContent(any(), eq("Sam"))).thenThrow(BookNotFoundException.class);

        module = mock(Module.class);
        when(module.getChapterSign()).thenReturn("<h4>");
        when(module.getVerseSign()).thenReturn("<p>");
        when(module.isChapterZero()).thenReturn(false);
        when(module.getID()).thenReturn("RST");

        searchProcessor = new MultiThreadSearchProcessor<>(repository);
    }

    @Test
    public void search() throws Exception {
        Map<String, String> results = searchProcessor.search(module, Arrays.asList("Gen", "Exo"), "бог");
        assertThat(results.size(), equalTo(76));
    }

    @Test
    public void searchBookNotFound() throws Exception {
        Map<String, String> results = searchProcessor.search(module, Collections.singletonList("Sam"), "бог");
        assertNotNull(results);
        assertThat(results.size(), equalTo(0));
    }
}