import java.util.*;
import java.time.LocalDate;
import java.util.Stack;

// ŞehirDüğümü sınıfı (Tree yapısı için)
class SehirDugumu {
    String sehirAdi;
    int sehirId;
    List<SehirDugumu> altSehirler;

    public SehirDugumu(String sehirAdi, int sehirId) {
        this.sehirAdi = sehirAdi;
        this.sehirId = sehirId;
        this.altSehirler = new ArrayList<>();
    }

    public void altSehirEkle(SehirDugumu altSehir) {
        altSehirler.add(altSehir);
    }
}

// Gönderi sınıfı (Linked List yapısı için)
class Gonderi {
    int gonderiId;
    String tarih;
    String durum;
    int teslimSuresi;
    String rota;
    Gonderi sonraki;

    public Gonderi(int gonderiId, String tarih, String durum, int teslimSuresi, String rota) {
        this.gonderiId = gonderiId;
        this.tarih = tarih;
        this.durum = durum;
        this.teslimSuresi = teslimSuresi;
        this.rota = rota;
        this.sonraki = null;
    }
}
class Musteri {
    int musteriId;
    String isim;
    Gonderi gonderiGecmisiBasi; // Linked List için
    Stack<Gonderi> gonderiStack; // Stack yapısı

    public Musteri(int musteriId, String isim) {
        this.musteriId = musteriId;
        this.isim = isim;
        this.gonderiGecmisiBasi = null;
        this.gonderiStack = new Stack<>(); // Stack başlatılma
    }

    public void gonderiEkle(Gonderi gonderi) {
        // Linked List'e ekleme
        if (gonderiGecmisiBasi == null) {
            gonderiGecmisiBasi = gonderi;
        } else {
            Gonderi mevcut = gonderiGecmisiBasi;
            while (mevcut.sonraki != null) {
                mevcut = mevcut.sonraki;
            }
            mevcut.sonraki = gonderi;
        }

        // Stack'e push işlemi
        gonderiStack.push(gonderi);
    }

    // Son 5 gönderiyi döndürme
    public List<Gonderi> sonBesGonderi() {
        List<Gonderi> sonGonderiler = new ArrayList<>();
        for (int i = 0; i < 5 && !gonderiStack.isEmpty(); i++) {
            sonGonderiler.add(gonderiStack.pop());
        }
        // Stack'ten alınanları geri ekleme
        Collections.reverse(sonGonderiler);
        for (Gonderi g : sonGonderiler) {
            gonderiStack.push(g);
        }
        return sonGonderiler;
    }
}


// Kargo sınıfı (Priority Queue için)
class Kargo implements Comparable<Kargo> {
    int gonderiId;
    int teslimSuresi;
    String durum;

    public Kargo(int gonderiId, int teslimSuresi, String durum) {
        this.gonderiId = gonderiId;
        this.teslimSuresi = teslimSuresi;
        this.durum = durum;
    }

    @Override
    public int compareTo(Kargo diger) {
        return Integer.compare(this.teslimSuresi, diger.teslimSuresi);
    }
}

class OnlineKargoTakip {

    private static SehirDugumu merkezSehir;
    private static PriorityQueue<Kargo> kargoKuyrugu = new PriorityQueue<>();
    private static List<Musteri> musteriler = new ArrayList<>();

    public static void main(String[] args) {
        sehirleriAyarla();
        ornekVeriEkle();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Kargo Takip Sistemi Menü ---");
            System.out.println("1. Yeni müşteri ekle");
            System.out.println("2. Gönderi ekle");
            System.out.println("3. Gönderi geçmişi sorgula");
            System.out.println("4. Teslimat rotasını göster");
            System.out.println("5. Kargo durumunu güncelle");
            System.out.println("6. Son 5 gönderiyi sorgula");
            System.out.println("7. Kargo Durumu Sorgulama");
            System.out.println("8. Çıkış");


            System.out.print("Seçiminiz: ");

            int secim = scanner.nextInt();
            scanner.nextLine();

            switch (secim) {
                case 1:
                    yeniMusteriEkle(scanner);
                    break;
                case 2:
                    gonderiEkle(scanner);
                    break;
                case 3:
                    gonderiGecmisiSorgula(scanner);
                    break;
                case 4:
                    teslimatRotasiniGoster(scanner);
                    break;
                case 5:
                    kargoDurumGuncelle(scanner);
                    break;
                case 6:
                    sonBesGonderiyiSorgula(scanner);
                    break;

                case 7:
                    kargoDurumuSorgula(scanner);
                case 8:
                    System.out.println("Çıkış yapılıyor...");
                    return;
                default:
                    System.out.println("Geçersiz seçim. Lütfen tekrar deneyin.");
            }
        }
    }
    private static void kargoDurumuSorgula(Scanner scanner) {
        System.out.println("Kargo Durumu Seçimi:");
        System.out.println("1. Teslim Edilmiş Kargoları Ara (Binary Search)");
        System.out.println("2. Teslim Edilmemiş Kargoları Listele (Quick Sort)");
        System.out.print("Seçiminiz: ");
        int secim = scanner.nextInt();
        scanner.nextLine();

        List<Gonderi> tumGonderiler = new ArrayList<>();
        for (Musteri musteri : musteriler) {
            Gonderi mevcut = musteri.gonderiGecmisiBasi;
            while (mevcut != null) {
                tumGonderiler.add(mevcut);
                mevcut = mevcut.sonraki;
            }
        }

        if (secim == 1) {
            List<Gonderi> teslimEdilmis = new ArrayList<>();
            for (Gonderi g : tumGonderiler) {
                if (g.durum.equals("Teslim Edildi")) {
                    teslimEdilmis.add(g);
                }
            }
            teslimEdilmis.sort(Comparator.comparingInt(g -> g.gonderiId));
            System.out.print("Aramak istediğiniz Kargo ID: ");
            int arananId = scanner.nextInt();
            Gonderi sonuc = binarySearch(teslimEdilmis, arananId);
            if (sonuc != null) {
                System.out.println("Bulunan Kargo: " +
                        "ID: " + sonuc.gonderiId + ", Tarih: " + sonuc.tarih +
                        ", Durum: " + sonuc.durum + ", Rota: " + sonuc.rota);
            } else {
                System.out.println("Kargo bulunamadı.");
            }
        } else if (secim == 2) {
            List<Gonderi> teslimEdilmemis = new ArrayList<>();
            for (Gonderi g : tumGonderiler) {
                if (!g.durum.equals("Teslim Edildi")) {
                    teslimEdilmemis.add(g);
                }
            }
            quickSort(teslimEdilmemis, 0, teslimEdilmemis.size() - 1);
            System.out.println("Teslim Edilmemiş Kargolar:");
            for (Gonderi g : teslimEdilmemis) {
                System.out.println("ID: " + g.gonderiId + ", Tarih: " + g.tarih +
                        ", Durum: " + g.durum + ", Rota: " + g.rota);
            }
        } else {
            System.out.println("Geçersiz seçim.");
        }
    }

    private static void sonBesGonderiyiSorgula(Scanner scanner) {
        System.out.print("Müşteri ID: ");
        int musteriId = scanner.nextInt();
        scanner.nextLine();

        // Müşteriyi bulma
        Musteri musteri = null;
        for (Musteri m : musteriler) {
            if (m.musteriId == musteriId) {
                musteri = m;
                break;
            }
        }

        if (musteri == null) {
            System.out.println("Müşteri bulunamadı.");
            return;
        }

        List<Gonderi> sonGonderiler = musteri.sonBesGonderi();
        if (sonGonderiler.isEmpty()) {
            System.out.println("Gönderim geçmişi yok.");
        } else {
            System.out.println("Son 5 Gönderi:");
            for (Gonderi gonderi : sonGonderiler) {
                System.out.println("Gönderi ID: " + gonderi.gonderiId +
                        ", Durum: " + gonderi.durum +
                        ", Tarih: " + gonderi.tarih +
                        ", Teslimat Süresi: " + gonderi.teslimSuresi);
            }
        }
    }

    private static Gonderi binarySearch(List<Gonderi> liste, int arananId) {
        int sol = 0, sag = liste.size() - 1;
        while (sol <= sag) {
            int orta = sol + (sag - sol) / 2;
            if (liste.get(orta).gonderiId == arananId) {
                return liste.get(orta);
            }
            if (liste.get(orta).gonderiId < arananId) {
                sol = orta + 1;
            } else {
                sag = orta - 1;
            }
        }
        return null;
    }

    private static void quickSort(List<Gonderi> liste, int baslangic, int bitis) {
        if (baslangic < bitis) {
            int pivotIndex = partition(liste, baslangic, bitis);
            quickSort(liste, baslangic, pivotIndex - 1);
            quickSort(liste, pivotIndex + 1, bitis);
        }
    }

    private static int partition(List<Gonderi> liste, int baslangic, int bitis) {
        int pivot = liste.get(bitis).gonderiId;
        int i = baslangic - 1;
        for (int j = baslangic; j < bitis; j++) {
            if (liste.get(j).gonderiId <= pivot) {
                i++;
                Collections.swap(liste, i, j);
            }
        }
        Collections.swap(liste, i + 1, bitis);
        return i + 1;
    }

    private static void yeniMusteriEkle(Scanner scanner) {
        System.out.print("Müşteri ID: ");
        int musteriId = scanner.nextInt();
        scanner.nextLine();

        for (Musteri m : musteriler) {
            if (m.musteriId == musteriId) {
                System.out.println("Bu ID'ye sahip bir müşteri zaten mevcut.");
                return;
            }
        }

        System.out.print("Müşteri İsmi: ");
        String isim = scanner.nextLine();

        Musteri musteri = new Musteri(musteriId, isim);
        musteriler.add(musteri);
        System.out.println("Yeni müşteri eklendi.");
    }

    private static void gonderiEkle(Scanner scanner) {
        System.out.print("Müşteri ID: ");
        int musteriId = scanner.nextInt();
        scanner.nextLine();

        Musteri musteri = null;
        for (Musteri m : musteriler) {
            if (m.musteriId == musteriId) {
                musteri = m;
                break;
            }
        }

        if (musteri == null) {
            System.out.println("Müşteri bulunamadı.");
            return;
        }

        System.out.print("Gönderi ID: ");
        int gonderiId = scanner.nextInt();
        scanner.nextLine();

        Gonderi mevcut = musteri.gonderiGecmisiBasi;
        while (mevcut != null) {
            if (mevcut.gonderiId == gonderiId) {
                System.out.println("Bu ID'ye sahip bir gönderi zaten mevcut.");
                return;
            }
            mevcut = mevcut.sonraki;
        }

        String tarih;
        while (true) {
            System.out.print("Tarih (YYYY-MM-DD): ");
            tarih = scanner.nextLine();
            if (tarih.matches("\\d{4}-\\d{2}-\\d{2}")) {
                break;
            } else {
                System.out.println("Geçersiz tarih formatı. Lütfen 'YYYY-MM-DD' formatında giriniz.");
            }
        }

        String durum = "Teslim Edilmedi";
        System.out.println("Gönderi başarıyla eklendi. İlk durum: Teslim Edilmedi");

        // Hedef şehrin ID'sini alma
        System.out.print("1 - İzmir\n" +
                "2 - Manisa\n" +
                "4 - Kütahya\n" +
                "8 - Afyonkarahisar\n" +
                "5 - Uşak\n" +
                "3 - Aydın\n" +
                "6 - Denizli\n" +
                "7 - Muğla\n" +
                "+Hedef şehir numarasını girin: ");
        int sehirId = scanner.nextInt();

// Şehir ID'nin geçerliliğini kontrol etme
        if (!sehirVarMi(sehirId)) {
            System.out.println("Geçersiz şehir numarası.");
            return;
        }

// Teslimat süresini hesaplama
        int teslimSuresi = teslimatSuresiHesapla(sehirId);
        if (teslimSuresi == -1) {
            System.out.println("Teslimat süresi hesaplanamadı.");
            return;
        }

// Gönderiyi oluşturma
        Gonderi gonderi = new Gonderi(gonderiId, tarih, durum, teslimSuresi, belirleRota(sehirId));
        musteri.gonderiEkle(gonderi);
        System.out.println("Gönderi eklendi. Teslimat süresi: " + teslimSuresi + " gün");

    }

    private static void gonderiGecmisiSorgula(Scanner scanner) {
        System.out.println("1. Tüm gönderileri listele");
        System.out.println("2. Belirli bir müşterinin gönderi geçmişini listele");
        System.out.print("Seçiminiz: ");
        int secim = scanner.nextInt();
        scanner.nextLine();

        if (secim == 1) {
            System.out.println("Tüm Gönderiler:");
            for (Musteri musteri : musteriler) {
                Gonderi mevcut = musteri.gonderiGecmisiBasi;
                while (mevcut != null) {
                    System.out.println("Müşteri ID: " + musteri.musteriId +
                            ", Gönderi ID: " + mevcut.gonderiId +
                            ", Tarih: " + mevcut.tarih +
                            ", Durum: " + mevcut.durum +
                            ", Teslim Süresi: " + mevcut.teslimSuresi +
                            ", Rota: " + mevcut.rota);
                    mevcut = mevcut.sonraki;
                }
            }
        } else if (secim == 2) {
            System.out.print("Müşteri ID girin: ");
            int musteriId = scanner.nextInt();
            scanner.nextLine();

            for (Musteri musteri : musteriler) {
                if (musteri.musteriId == musteriId) {
                    System.out.println("Müşteri ID: " + musteriId);
                    Gonderi mevcut = musteri.gonderiGecmisiBasi;
                    while (mevcut != null) {
                        System.out.println("Gönderi ID: " + mevcut.gonderiId +
                                ", Tarih: " + mevcut.tarih +
                                ", Durum: " + mevcut.durum +
                                ", Teslim Süresi: " + mevcut.teslimSuresi +
                                ", Rota: " + mevcut.rota);
                        mevcut = mevcut.sonraki;
                    }
                    return;
                }
            }
            System.out.println("Müşteri bulunamadı.");
        } else {
            System.out.println("Geçersiz seçim.");
        }
    }

    private static void kargoDurumGuncelle(Scanner scanner) {
        System.out.print("Müşteri ID: ");
        int musteriId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Gönderi ID: ");
        int gonderiId = scanner.nextInt();
        scanner.nextLine();

        for (Musteri musteri : musteriler) {
            if (musteri.musteriId == musteriId) {
                Gonderi mevcut = musteri.gonderiGecmisiBasi;
                while (mevcut != null) {
                    if (mevcut.gonderiId == gonderiId) {
                        System.out.println("Mevcut Durum: " + mevcut.durum);
                        System.out.println("Yeni durumu seçin:");
                        System.out.println("1. İşleme Alındı");
                        System.out.println("2. Yolda");
                        System.out.println("3. Teslim Edildi");
                        System.out.print("Seçiminiz: ");
                        int yeniDurum = scanner.nextInt();
                        scanner.nextLine();

                        switch (yeniDurum) {
                            case 1:
                                mevcut.durum = "İşleme Alındı ";
                                break;
                            case 2:
                                mevcut.durum = "Yolda";
                                break;
                            case 3:
                                mevcut.durum = "Teslim Edildi";
                                break;
                            default:
                                System.out.println("Geçersiz seçim.");
                                return;
                        }
                        System.out.println("Durum güncellendi: " + mevcut.durum);
                        return;
                    }
                    mevcut = mevcut.sonraki;
                }
            }
        }
        System.out.println("Gönderi bulunamadı.");
    }

    private static void sehirNumaralariniListele() {
        System.out.println("Mevcut şehirler:");
        sehirleriYazdir(merkezSehir);
    }

    private static void sehirleriYazdir(SehirDugumu sehir) {
        System.out.println(sehir.sehirId + " - " + sehir.sehirAdi);
        for (SehirDugumu altSehir : sehir.altSehirler) {
            sehirleriYazdir(altSehir);
        }
    }

    private static boolean sehirVarMi(int sehirId) {
        return sehirBul(sehirId, merkezSehir) != null;
    }

    private static SehirDugumu sehirBul(int sehirId, SehirDugumu sehir) {
        if (sehir.sehirId == sehirId) {
            return sehir;
        }
        for (SehirDugumu altSehir : sehir.altSehirler) {
            SehirDugumu bulunan = sehirBul(sehirId, altSehir);
            if (bulunan != null) {
                return bulunan;
            }
        }
        return null;
    }

    private static String belirleRota(int hedefSehirId) {
        List<String> rota = new ArrayList<>();
        if (rotaBul(merkezSehir, hedefSehirId, rota)) {
            return String.join(" -> ", rota);
        } else {
            return "Rota bulunamadı.";
        }
    }

    private static boolean rotaBul(SehirDugumu sehir, int hedefSehirId, List<String> rota) {
        rota.add(sehir.sehirAdi);

        if (sehir.sehirId == hedefSehirId) {
            return true;
        }

        for (SehirDugumu altSehir : sehir.altSehirler) {
            if (rotaBul(altSehir, hedefSehirId, rota)) {
                return true;
            }
        }

        rota.remove(rota.size() - 1);
        return false;
    }

    private static int teslimatSuresiHesapla(int hedefSehirId) {
        // Şehri bul
        SehirDugumu hedefSehir = sehirBul(hedefSehirId, merkezSehir);
        if (hedefSehir == null) {
            System.out.println("Hedef şehir bulunamadı.");
            return -1; // Geçersiz şehir
        }
        // Derinliği hesapla
        return hesaplaDerinlik(merkezSehir, hedefSehirId, 0);
    }

    private static int hesaplaDerinlik(SehirDugumu sehir, int hedefSehirId, int derinlik) {
        if (sehir.sehirId == hedefSehirId) {
            return derinlik; // Şehir bulundu, derinliği döndür
        }
        for (SehirDugumu altSehir : sehir.altSehirler) {
            int sonuc = hesaplaDerinlik(altSehir, hedefSehirId, derinlik + 1);
            if (sonuc != -1) {
                return sonuc; // Hedef şehir bulunduysa derinlik döndürülür
            }
        }
        return -1; // Hedef şehir alt dallarda bulunamadı
    }


    private static void teslimatRotasiniGoster(Scanner scanner) {
        sehirNumaralariniListele();
        System.out.print("Hedef şehir numarasını girin: ");
        int hedefSehirId = scanner.nextInt();

        if (sehirVarMi(hedefSehirId)) {
            // Rota ve teslimat süresini hesapla
            String rota = belirleRota(hedefSehirId);
            int teslimSuresi = teslimatSuresiHesapla(hedefSehirId);
            if (teslimSuresi != -1) {
                System.out.println("Teslimat Rotası: " + rota);
                System.out.println("Tahmini Teslimat Süresi: " + teslimSuresi + " gün");
            } else {
                System.out.println("Teslimat süresi hesaplanamadı.");
            }
        } else {
            System.out.println("Hedef şehir bulunamadı.");
        }
    }


    private static void sehirleriAyarla() {
        merkezSehir = new SehirDugumu("İzmir", 1);

        SehirDugumu manisa = new SehirDugumu("Manisa", 2);
        SehirDugumu aydin = new SehirDugumu("Aydın", 3);
        SehirDugumu kutahya = new SehirDugumu("Kütahya", 4);
        SehirDugumu usak = new SehirDugumu("Uşak", 5);
        SehirDugumu denizli = new SehirDugumu("Denizli", 6);
        SehirDugumu mugla = new SehirDugumu("Muğla", 7);
        SehirDugumu afyon = new SehirDugumu("Afyonkarahisar", 8);

        merkezSehir.altSehirEkle(manisa);
        merkezSehir.altSehirEkle(aydin);
        manisa.altSehirEkle(kutahya);
        manisa.altSehirEkle(usak);
        aydin.altSehirEkle(denizli);
        aydin.altSehirEkle(mugla);
        kutahya.altSehirEkle(afyon);
    }

    private static void ornekVeriEkle() {
        Musteri musteri = new Musteri(1, "Ahmet Yılmaz");
        musteri.gonderiEkle(new Gonderi(101, "2023-12-01", "Teslim Edildi", 3, "İzmir -> Manisa -> Aydın"));
        musteriler.add(musteri);

        kargoKuyrugu.add(new Kargo(102, 2, "Yolda"));
        kargoKuyrugu.add(new Kargo(103, 1, "Yolda"));
    }
}
