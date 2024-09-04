package elfak.mosis.projekat.Screens.Signup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PrivacyScreen(onBtnClick:()->Unit)
{
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Uslovi Privatnosti",fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Text(
                text = buildAnnotatedString {
                    append("Prikupljanje i Korišćenje Podataka\n")
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold), start = 0, end = 30)

                    append("Kada se registrujete za našu aplikaciju, možemo prikupljati sledeće informacije:\n")
                    append("Lične podatke kao što su ime, prezime, korisničko ime, email adresa, broj telefona, i lozinka. Informacije o uređaju, uključujući IP adresu, operativni sistem i tip pretraživača.\n")

                    append("Ove informacije koristimo kako bismo:\n")
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold), start = length - 39, end = length)

                    append("Omogućili pristup našoj aplikaciji i njenim funkcijama.\n")
                    append("Poboljšali korisničko iskustvo.\n")
                    append("Pratili i analizirali upotrebu aplikacije radi unapređenja usluga.\n")

                    append("Čuvanje i Zaštita Podataka\n")
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold), start = length - 27, end = length)

                    append("Vaše podatke čuvamo na sigurnim serverima i preduzimamo odgovarajuće tehničke i organizacione mere kako bismo zaštitili vaše podatke od neovlašćenog pristupa ili zloupotrebe.\n")

                    append("Deljenje Podataka\n")
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold), start = length - 18, end = length)

                    append("Vaše lične podatke nećemo deliti s trećim stranama osim u sledećim situacijama:\n")
                    append("Kada imamo vašu izričitu saglasnost.\n")
                    append("Kada smo obavezni po zakonu.\n")

                    append("Saglasnost\n")
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold), start = length - 11, end = length)

                    append("Prilikom registracije, klikom na dugme Registruj se potvrđujete da ste pročitali, razumeli i saglasni s našim  ")
                    append("Uslovima privatnosti.\n")
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold), start = length - 22, end = length)

                }
                    /*
                    text = "Prikupljanje i Korišćenje Podataka\n"+
                            "Kada se registrujete za našu aplikaciju, možemo prikupljati sledeće informacije:\n"+
                            "Lične podatke kao što su ime, prezime, korisničko ime, email adresa, broj telefona, i lozinka.\n"+
                            "Informacije o uređaju, uključujući IP adresu, operativni sistem i tip pretraživača. \n"+
                            "Ove informacije koristimo kako bismo:\n"+
                            "Omogućili pristup našoj aplikaciji i njenim funkcijama.\n"+
                            "Poboljšali korisničko iskustvo.\n"+
                            "Pratili i analizirali upotrebu aplikacije radi unapređenja usluga.\n"+
                            "Čuvanje i Zaštita Podataka\n"+
                            "Vaše podatke čuvamo na sigurnim serverima i preduzimamo odgovarajuće tehničke i organizacione mere\n"+
                            "kako bismo zaštitili vaše podatke od neovlašćenog pristupa ili zloupotrebe.\n"+
                            "Deljenje Podataka\n"+
                            "Vaše lične podatke nećemo deliti s trećim stranama osim u sledećim situacijama:\n"+
                            "Kada imamo vašu izričitu saglasnost.\n"+
                            "Kada smo obavezni po zakonu.\n"+
                            "Saglasnost\n"+
                            "Prilikom registracije, klikom na dugme Registruj se potvrđujete da ste pročitali, razumeli i saglasni s našim Uslovima privatnosti.\n"

                */)
            Spacer(Modifier.height(32.dp))
            Button(onClick = onBtnClick, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Nazad")
            }
        }
    }
}