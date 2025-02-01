package de.lehmannet.om.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lehmannet.om.Constellation;
import de.lehmannet.om.EquPosition;

public class ConstellationCalculatorTest {

    private static Logger log = LoggerFactory.getLogger(ConstellationCalculatorTest.class);

    private Map<String, List<String>> messier = messierPositions();

    ConstellationCalculator calculator = new ConstellationCalculator();

    @Test
    public void shouldReturnAndromedaWhenPositionIsInAndromeda() {
        // 00h 42m 44.3s[1]
        // Declination +4100b0 16′ 9″
        // M31 coordinates

        var pos = new EquPosition("00h 42m 44.3s", "+41\u00b0 16' 09\"");
        var result = calculator.getConstellation(pos, 2000.0);

        assertEquals(Constellation.ANDROMEDA, result);
    }

    @Test
    public void shouldReturnConstellationDefinedInCatalogForMessierObjects() {

        for (Entry<String, List<String>> entrySet : messier.entrySet()) {

            var objectMessier = entrySet.getKey();

            var data = entrySet.getValue();
            var expected = Constellation.getConstellationByAbbOrName(data.get(0));
            var equPos = new EquPosition(data.get(1), data.get(2));

            var result = calculator.getConstellation(equPos, 2000.0);
            assertEquals(expected, result);

        }

    }

    private Map<String, List<String>> messierPositions() {

        var messier = new HashMap<String, List<String>>();

        messier.put("M1", List.of("Tau", "05h34m31s", "22\u00b000'52\""));
        messier.put("M2", List.of("Aqr", "21h33m29s", "-00\u00b049'23\""));
        messier.put("M3", List.of("CVn", "13h42m11s", "28\u00b022'32\""));
        messier.put("M4", List.of("Sco", "16h23m35s", "-26\u00b031'31\""));
        messier.put("M5", List.of("Ser", "15h18m33s", "02\u00b004'58\""));
        messier.put("M6", List.of("Sco", "17h40m21s", "-32\u00b012'15\""));
        messier.put("M7", List.of("Sco", "17h53m51s", "-34\u00b047'34\""));
        messier.put("M8", List.of("Sgr", "18h04m04s", "-24\u00b023'49\""));
        messier.put("M9", List.of("Oph", "17h19m12s", "-18\u00b030'59\""));
        messier.put("M10", List.of("Oph", "16h57m09s", "-04\u00b005'58\""));
        messier.put("M11", List.of("Sct", "18h51m06s", "-06\u00b016'12\""));
        messier.put("M12", List.of("Oph", "16h47m12s", "-01\u00b056'52\""));
        messier.put("M13", List.of("Her", "16h41m41s", "36\u00b027'36\""));
        messier.put("M14", List.of("Oph", "17h37m36s", "-03\u00b014'46\""));
        messier.put("M15", List.of("Peg", "21h29m58s", "12\u00b010'00\""));
        messier.put("M16", List.of("Ser", "18h18m48s", "-13\u00b048'26\""));
        messier.put("M17", List.of("Sgr", "18h20m47s", "-16\u00b010'18\""));
        messier.put("M18", List.of("Sgr", "18h19m58s", "-17\u00b006'07\""));
        messier.put("M19", List.of("Oph", "17h02m38s", "-26\u00b016'05\""));
        messier.put("M20", List.of("Sgr", "18h02m21s", "-23\u00b001'38\""));
        messier.put("M21", List.of("Sgr", "18h04m13s", "-22\u00b029'24\""));
        messier.put("M22", List.of("Sgr", "18h36m24s", "-23\u00b054'12\""));
        messier.put("M23", List.of("Sgr", "17h57m05s", "-18\u00b059'07\""));
        messier.put("M24", List.of("Sgr", "18h18m27s", "-18\u00b024'22\""));
        messier.put("M25", List.of("Sgr", "18h31m45s", "-19\u00b006'46\""));
        messier.put("M26", List.of("Sct", "18h45m19s", "-09\u00b023'01\""));
        messier.put("M27", List.of("Vul", "19h59m36s", "22\u00b043'17\""));
        messier.put("M28", List.of("Sgr", "18h24m33s", "-24\u00b052'12\""));
        messier.put("M29", List.of("Cyg", "20h23m58s", "38\u00b030'28\""));
        messier.put("M30", List.of("Cap", "21h40m22s", "-23\u00b010'45\""));
        messier.put("M31", List.of("And", "00h42m44s", "41\u00b016'06\""));
        messier.put("M32", List.of("And", "00h42m42s", "40\u00b051'52\""));
        messier.put("M33", List.of("Tri", "01h33m51s", "30\u00b039'37\""));
        messier.put("M34", List.of("Per", "02h42m07s", "42\u00b044'46\""));
        messier.put("M35", List.of("Gem", "06h08m56s", "24\u00b021'28\""));
        messier.put("M36", List.of("Aur", "05h36m18s", "34\u00b008'27\""));
        messier.put("M37", List.of("Aur", "05h52m18s", "32\u00b033'11\""));
        messier.put("M38", List.of("Aur", "05h28m43s", "35\u00b051'18\""));
        messier.put("M39", List.of("Cyg", "21h31m48s", "48\u00b026'55\""));
        messier.put("M40", List.of("UMa", "12h22m16s", "58\u00b005'04\""));
        messier.put("M41", List.of("CMa", "06h46m00s", "-20\u00b045'15\""));
        messier.put("M42", List.of("Ori", "05h35m17s", "-05\u00b023'27\""));
        messier.put("M43", List.of("Ori", "05h35m31s", "-05\u00b016'03\""));
        messier.put("M44", List.of("Cnc", "08h40m22s", "19\u00b040'19\""));
        messier.put("M45", List.of("Tau", "03h47m28s", "24\u00b006'18\""));
        messier.put("M46", List.of("Pup", "07h41m47s", "-14\u00b048'36\""));
        messier.put("M47", List.of("Pup", "07h36m35s", "-14\u00b028'57\""));
        messier.put("M48", List.of("Hya", "08h13m43s", "-05\u00b045'02\""));
        messier.put("M49", List.of("Vir", "12h29m47s", "08\u00b000'00\""));
        messier.put("M50", List.of("Mon", "07h02m42s", "-08\u00b023'26\""));
        messier.put("M51", List.of("CVn", "13h29m52s", "47\u00b011'43\""));
        messier.put("M52", List.of("Cas", "23h24m50s", "61\u00b036'24\""));
        messier.put("M53", List.of("Com", "13h12m55s", "18\u00b010'08\""));
        messier.put("M54", List.of("Sgr", "18h55m03s", "-30\u00b028'42\""));
        messier.put("M55", List.of("Sgr", "19h39m59s", "-30\u00b057'44\""));
        messier.put("M56", List.of("Lyr", "19h16m35s", "30\u00b011'05\""));
        messier.put("M57", List.of("Lyr", "18h53m35s", "33\u00b001'43\""));
        messier.put("M58", List.of("Vir", "12h37m44s", "11\u00b049'05\""));
        messier.put("M59", List.of("Vir", "12h42m02s", "11\u00b038'48\""));
        messier.put("M60", List.of("Vir", "12h43m40s", "11\u00b033'07\""));
        messier.put("M61", List.of("Vir", "12h21m55s", "04\u00b028'24\""));
        messier.put("M62", List.of("Oph", "17h01m13s", "-30\u00b006'44\""));
        messier.put("M63", List.of("CVn", "13h15m49s", "42\u00b001'50\""));
        messier.put("M64", List.of("Com", "12h56m44s", "21\u00b040'58\""));
        messier.put("M65", List.of("Leo", "11h18m56s", "13\u00b005'37\""));
        messier.put("M66", List.of("Leo", "11h20m15s", "12\u00b059'28\""));
        messier.put("M67", List.of("Cnc", "08h51m20s", "11\u00b048'43\""));
        messier.put("M68", List.of("Hya", "12h39m28s", "-26\u00b044'34\""));
        messier.put("M69", List.of("Sgr", "18h31m23s", "-32\u00b020'53\""));
        messier.put("M70", List.of("Sgr", "18h43m13s", "-32\u00b017'31\""));
        messier.put("M71", List.of("Sge", "19h53m46s", "18\u00b046'42\""));
        messier.put("M72", List.of("Aqr", "20h53m28s", "-12\u00b032'13\""));
        messier.put("M73", List.of("Aqr", "20h58m56s", "-12\u00b038'08\""));
        messier.put("M74", List.of("Psc", "01h36m42s", "15\u00b047'03\""));
        messier.put("M75", List.of("Sgr", "20h06m05s", "-21\u00b055'17\""));
        messier.put("M76", List.of("Per", "01h42m18s", "51\u00b034'16\""));
        messier.put("M77", List.of("Cet", "02h42m41s", "-00\u00b000'47\""));
        messier.put("M78", List.of("Ori", "05h46m46s", "00\u00b004'45\""));
        messier.put("M79", List.of("Lep", "05h24m11s", "-24\u00b031'27\""));
        messier.put("M80", List.of("Sco", "16h17m03s", "-22\u00b058'30\""));
        messier.put("M81", List.of("UMa", "09h55m33s", "69\u00b003'55\""));
        messier.put("M82", List.of("UMa", "09h55m51s", "69\u00b040'43\""));
        messier.put("M83", List.of("Hya", "13h37m00s", "-29\u00b052'04\""));
        messier.put("M84", List.of("Vir", "12h25m07s", "12\u00b053'13\""));
        messier.put("M85", List.of("Com", "12h25m24s", "18\u00b011'27\""));
        messier.put("M86", List.of("Vir", "12h26m12s", "12\u00b056'47\""));
        messier.put("M87", List.of("Vir", "12h30m49s", "12\u00b023'26\""));
        messier.put("M88", List.of("Com", "12h31m59s", "14\u00b025'15\""));
        messier.put("M89", List.of("Vir", "12h35m40s", "12\u00b033'23\""));
        messier.put("M90", List.of("Vir", "12h36m50s", "13\u00b009'45\""));
        messier.put("M91", List.of("Com", "12h35m27s", "14\u00b029'48\""));
        messier.put("M92", List.of("Her", "17h17m08s", "43\u00b008'11\""));
        messier.put("M93", List.of("Pup", "07h44m29s", "-23\u00b051'11\""));
        messier.put("M94", List.of("CVn", "12h50m53s", "41\u00b007'12\""));
        messier.put("M95", List.of("Leo", "10h43m58s", "11\u00b042'13\""));
        messier.put("M96", List.of("Leo", "10h46m46s", "11\u00b049'25\""));
        messier.put("M97", List.of("UMa", "11h14m48s", "55\u00b001'07\""));
        messier.put("M98", List.of("Com", "12h13m48s", "14\u00b054'00\""));
        messier.put("M99", List.of("Com", "12h18m50s", "14\u00b025'01\""));
        messier.put("M100", List.of("Com", "12h22m55s", "15\u00b049'21\""));
        messier.put("M101", List.of("UMa", "14h03m12s", "54\u00b020'55\""));
        messier.put("M102", List.of("Dra", "15h06m29s", "55\u00b045'47\""));
        messier.put("M103", List.of("Cas", "01h33m22s", "60\u00b039'29\""));
        messier.put("M104", List.of("Vir", "12h39m59s", "-11\u00b037'23\""));
        messier.put("M105", List.of("Leo", "10h47m50s", "12\u00b034'53\""));
        messier.put("M106", List.of("CVn", "12h18m58s", "47\u00b018'15\""));
        messier.put("M107", List.of("Oph", "16h32m32s", "-13\u00b003'10\""));
        messier.put("M108", List.of("UMa", "11h11m31s", "55\u00b040'24\""));
        messier.put("M109", List.of("UMa", "11h57m36s", "53\u00b022'28\""));
        messier.put("M110", List.of("And", "00h40m22s", "41\u00b041'26\""));
        return messier;
    }
}
