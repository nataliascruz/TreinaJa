# TreinaJá

## 📋 Descrição

**TreinaJá** é um aplicativo Android desenvolvido para ajudar os usuários a gerenciar seus treinos físicos de forma prática e eficiente. O app permite criar, editar e acompanhar diferentes tipos de treinos (**A**, **B** e **C**), com exercícios específicos para cada um. Além disso, oferece funcionalidades para o gerenciamento do perfil do usuário, incluindo informações pessoais e objetivos de treino.

---

## 🛠️ Tecnologias e Linguagens

- **Linguagem principal:** Java  
- **Banco de dados:** SQLite  
- **Ambiente de desenvolvimento:** Android Studio  
- **Arquitetura:** MVC (Model-View-Controller)  
- **Bibliotecas:** Android SDK

---

## 🚀 Funcionalidades Principais

### 📌 Gerenciamento de Treinos
- Criação de treinos (A, B e C).
- Adição e remoção de exercícios.
- Edição de exercícios existentes.
- Limite de 8 exercícios por treino.
- Visualização detalhada dos treinos.

### 📌 Perfil do Usuário
- Cadastro de informações pessoais: nome, idade, peso e altura.
- Definição de objetivos de treino.
- Edição e exclusão de dados.

### 📌 Navegação Intuitiva
- Sistema de navegação entre telas.
- Feedback visual para ações do usuário.

### 📌 Validação de Dados
- Validação de campos de entrada.
- Formatação automática de dados.
- Mensagens de erro claras e objetivas.

---

## 📥 Como Instalar

### ✅ Pré-requisitos

Antes de instalar e executar o TreinaJá, certifique-se de que possui:

- ✅ **Java Development Kit (JDK)** instalado e configurado.  
  → Baixe em: [https://www.oracle.com/java/technologies/javase-downloads.html](https://www.oracle.com/java/technologies/javase-downloads.html)

- ✅ **Android Studio** instalado.  
  → Baixe em: [https://developer.android.com/studio](https://developer.android.com/studio)

- ✅ **Emulador Android** configurado ou um **dispositivo físico** conectado via USB (com depuração USB ativada).

---

### ⚙️ Passos de Instalação

1. Clone o repositório do projeto:

git clone https://github.com/nataliascruz/TreinaJa.git

2. Abra o projeto no Android Studio.

3. Sincronize o projeto com o Gradle.

4. Execute o aplicativo em um emulador ou dispositivo Android.
   
---

📖 Como Usar

🏠 Tela Inicial (FormLogin)
Acesse "Meu Treino" para gerenciar seus treinos.

Acesse "Meu Perfil" para editar suas informações pessoais.

💪 Gerenciamento de Treinos (MeuTreino)
Selecione um treino (A, B ou C).

Adicione novos exercícios, informando nome, séries e repetições.

Edite ou remova exercícios existentes.

Marque o treino como concluído ao finalizá-lo.

👤 Perfil do Usuário (Perfil)
Preencha suas informações pessoais.

Defina seu objetivo de treino.

Salve ou limpe os dados conforme necessário.

🗂️ Estrutura do Projeto
O projeto está organizado em pacotes principais:

activities

AdicionarTreino: Tela para adicionar novos exercícios.

Concluido: Tela de conclusão do treino.

EditarExercicio: Tela para editar exercícios.

FormLogin: Tela inicial de autenticação.

MeuTreino: Tela principal de gerenciamento dos treinos.

Perfil: Tela para gerenciamento do perfil do usuário.

NavigationActivity: Classe base para navegação entre telas.

database

DataBaseHelper: Classe responsável pelo gerenciamento do banco de dados SQLite.

🗄️ Banco de Dados (SQLite)
O aplicativo utiliza um banco de dados SQLite com três tabelas principais:

usuario: armazena informações do perfil do usuário.

treino: armazena os treinos criados (A, B e C).

exercicio: armazena os exercícios associados a cada treino.

As tabelas estão relacionadas por chaves estrangeiras, garantindo a integridade dos dados.

👥 Equipe de Desenvolvimento
Emelly Costa Sales – 2050671

Lucas Mota Silva – 1898181

Natalia Souza da Cruz – 1987065

Wallace Oliveira Wanderlei Dias – 2031549

## Conclusão

TreinaJá é uma solução completa para o gerenciamento de treinos físicos, oferecendo uma interface intuitiva e funcionalidades robustas para auxiliar os usuários no acompanhamento de seus progressos. O aplicativo foi desenvolvido seguindo boas práticas de programação Android, incluindo validação de dados, tratamento de erros e uso eficiente de banco de dados.
