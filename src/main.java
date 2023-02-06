import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class main {
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final int PROGRAM_CHANGE = 0xC0;
    public static final int CONTROL_CHANGE = 0xB0;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public static void main(String[] args) throws Exception {

        Sequence sequence = MidiSystem.getSequence(new File("./beethoven_tempest_op31_no2_3rdmvt_PNO.mid"));

        System.out.println("sequence DivisionType : " + sequence.getDivisionType());
        System.out.println("sequence MicrosecondLength : " + sequence.getMicrosecondLength());
        System.out.println("sequence Resolution : " + sequence.getResolution());
        System.out.println("sequence TickLength : " + sequence.getTickLength());
        ArrayList<String[]> eventList = new ArrayList<>();
        ArrayList<String[]> noteList = new ArrayList<>();

        int trackNumber = 0;

        for (Track track : sequence.getTracks()) {

            trackNumber++;
            System.out.println("Track " + trackNumber + ": size = " + track.size());
            System.out.println("Track Ticks : " + track.ticks());

            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                //System.out.print("@" + event.getTick() + " ");
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    //System.out.print("Channel: " + sm.getChannel() + " ");
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        //System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);

                        if(velocity > 0) {
                            String[] str = {String.valueOf(event.getTick()), "ON", noteName, String.valueOf(velocity)};
                            eventList.add(str);
                        } else if(velocity == 0) {
                            String[] str = {String.valueOf(event.getTick()), "OFF", noteName, String.valueOf(velocity)};
                            eventList.add(str);
                        }
                    } else if (sm.getCommand() == NOTE_OFF) {
                        int key = sm.getData1();
                        int octave = (key / 12) - 1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        //System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);

                        String[] str = {String.valueOf(event.getTick()), "OFF", noteName, String.valueOf(velocity)};
                        eventList.add(str);
                    } else if (sm.getCommand() == PROGRAM_CHANGE) {
                        System.out.println("Command: PROGRAM_CHANGE");
                    } else if (sm.getCommand() == CONTROL_CHANGE) {
                        System.out.println("Command: CONTROL_CHANGE");
                    } else {
                        System.out.println("Command:" + sm.getCommand());
                    }
                } else if(message instanceof MetaMessage) {
                    MetaMessage mm = (MetaMessage) message;
                    convertMetaMessage(mm);
                } else {
                    System.out.println("Other message: " + message.getClass());
                }
            }
        }
    }

    static public void convertMetaMessage(MetaMessage metaMessage) {
        byte[] data = metaMessage.getData();
        int metaType = metaMessage.getType();

        StringBuilder str = new StringBuilder();

        for(byte b : data) {
            System.out.println("data :: " + b);
            str.append((char)b);
        }

        if(metaType == 0x00) {
            System.out.println("Meta Type : " + metaType);
        }else if(metaType == 0x01) {
            System.out.println("Meta Type : " + metaType);
        }else if(metaType == 0x02) {
            System.out.println("Meta Type : " + metaType);
        }else if(metaType == 0x03) {
            System.out.println("Meta Type : " + metaType);
            System.out.println("Meta Content : 트랙 이름");
            System.out.println("Meta Data : " + str);
        }else if(metaType == 0x04) {
            System.out.println("Meta Type : " + metaType);
        }else if(metaType == 0x05) {
            System.out.println("Meta Type : " + metaType);
        }else if(metaType == 0x06) {
            System.out.println("Meta Type : " + metaType);
        }else if(metaType == 0x07) {
            System.out.println("Meta Type : " + metaType);
        }else if(metaType == 0x20) {
            System.out.println("Meta Type : " + metaType);
        }else if(metaType == 0x2F) {
            System.out.println("Meta Type : " + metaType);
            System.out.println("End of track");
        }else if(metaType == 0x51) {
            System.out.println("Meta Type : " + metaType);
            System.out.println("Meta Content : 템포 설정(비트당 마이크로초 수)");
            System.out.println("Meta Data : " + getTempoInBPM(metaMessage));
        }else if(metaType == 0x54) {
            System.out.println("Meta Type : " + metaType);
        }else if(metaType == 0x58) {
            System.out.println("Meta Type : " + metaType);
            System.out.println("Meta Content : 박자");
            System.out.println("Meta Data : " + Math.pow(2, data[1]) + "분의" + data[0] + "박" + " MIDI 클럭 " + data[2]);
        }else if(metaType == 0x59) {
            System.out.println("Meta Type : " + metaType);
            System.out.println("Meta Content : 키 시그니처 " + data[0] + " : " + data[1]);
        }else if(metaType == 0x7F) {
            System.out.println("Meta Type : " + metaType);
        }else {
            System.out.println("Meta Type else : " + metaType);
        }
    }

    /**
     * Get the tempo in BPM coded in a Tempo Midi message.
     *
     * @param mm Must be a tempo MetaMessage (type=81)
     * @return
     */
    static public int getTempoInBPM(MetaMessage mm)
    {
        byte[] data = mm.getData();
        if (mm.getType() != 81 || data.length != 3)
        {
            throw new IllegalArgumentException("mm=" + mm);
        }
        int mspq = ((data[0] & 0xff) << 16) | ((data[1] & 0xff) << 8) | (data[2] & 0xff);
        int tempo = Math.round(60000001f / mspq);
        return tempo;
    }
}
